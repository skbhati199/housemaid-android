import os
import re

def fix_glide_context(content, filepath):
    filename = os.path.basename(filepath)
    if "Activity.java" in filename:
        content = content.replace("Glide.with(itemView.getContext())", "Glide.with(this)")
    elif "Adapter" in filename:
        content = content.replace("Glide.with(itemView.getContext())", "Glide.with(context)")
    return content

def find_switch_blocks(content):
    # Find all "switch"
    blocks = []
    idx = 0
    while True:
        match = re.search(r"switch\s*\((.*?)\)\s*\{", content[idx:])
        if not match:
            break
        
        start_idx = idx + match.start()
        open_brace_idx = idx + match.end() - 1
        
        # count braces to find the end
        brace_count = 0
        end_idx = -1
        for i in range(open_brace_idx, len(content)):
            if content[i] == '{':
                brace_count += 1
            elif content[i] == '}':
                brace_count -= 1
                if brace_count == 0:
                    end_idx = i
                    break
        
        if end_idx != -1:
            var_name = match.group(1).strip()
            # If the var name has .getId(), strip it for the if condition, or just use it directly
            # Actually, var_name might be `v.getId()` or `id`
            blocks.append({
                'start': start_idx,
                'end': end_idx + 1,
                'var': var_name,
                'body': content[open_brace_idx+1:end_idx]
            })
            idx = end_idx + 1
        else:
            idx = open_brace_idx + 1
            
    return blocks

def convert_switch_to_if(block_info):
    var_name = block_info['var']
    body = block_info['body']
    
    if "case R.id." not in body:
        return None # No change
        
    # Split by case or default
    parts = re.split(r"(\bcase\s+R\.id\.[a-zA-Z0-9_]+\s*:|\bdefault\s*:)", body)
    if len(parts) <= 1:
        return None
        
    new_block = []
    is_first = True
    
    i = 1
    while i < len(parts):
        case_label = parts[i].strip()
        case_body = parts[i+1] if i+1 < len(parts) else ""
        
        # remove 'break;'
        case_body_clean = re.sub(r"\bbreak\s*;", "", case_body)
        
        if case_label.startswith("case"):
            id_match = re.search(r"R\.id\.[a-zA-Z0-9_]+", case_label)
            if id_match:
                r_id = id_match.group(0)
                if is_first:
                    new_block.append(f"if ({var_name} == {r_id}) {{")
                    is_first = False
                else:
                    new_block.append(f"}} else if ({var_name} == {r_id}) {{")
                new_block.append(case_body_clean)
        elif case_label.startswith("default"):
            if is_first:
                new_block.append(f"{{")
                is_first = False
            else:
                new_block.append(f"}} else {{")
            new_block.append(case_body_clean)
        
        i += 2
        
    if new_block:
        new_block.append(f"}}")
        
    return "\n".join(new_block)

def fix_switch_case(content):
    blocks = find_switch_blocks(content)
    # Apply replacements from back to front to preserve indices
    for block in reversed(blocks):
        replacement = convert_switch_to_if(block)
        if replacement is not None:
            content = content[:block['start']] + replacement + content[block['end']:]
    return content

java_dir = 'e:/projects/android-projects/housemaid-android-master/app/src/main/java'
for root, dirs, files in os.walk(java_dir):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            new_content = fix_glide_context(content, filepath)
            new_content = fix_switch_case(new_content)
            
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Refactored {filepath}")
