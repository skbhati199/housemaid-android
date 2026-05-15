import os
import re

def fix_glide_context(content, filepath):
    filename = os.path.basename(filepath)
    if "Activity.java" in filename:
        content = content.replace("Glide.with(itemView.getContext())", "Glide.with(this)")
    elif "Adapter" in filename:
        content = content.replace("Glide.with(itemView.getContext())", "Glide.with(context)")
    return content

def fix_switch_case(content):
    # This is a bit tricky, but since these switch cases are mostly standard:
    # switch (v.getId()) { case R.id.btn: ... break; ... }
    # We will use regex to find switch blocks that contain R.id
    
    pattern = r"switch\s*\(\s*([a-zA-Z0-9_]+)\.getId\(\)\s*\)\s*\{([\s\S]*?)\n\s*\}"
    
    def replacer(match):
        var_name = match.group(1)
        body = match.group(2)
        if "case R.id." not in body:
            return match.group(0) # don't touch
            
        # parse cases
        # split by 'case ' or 'default:'
        parts = re.split(r"(case\s+R\.id\.[a-zA-Z0-9_]+\s*:|default\s*:)", body)
        
        if len(parts) <= 1:
            return match.group(0)
            
        new_block = []
        is_first = True
        
        # parts[0] is everything before the first case
        i = 1
        while i < len(parts):
            case_label = parts[i].strip()
            case_body = parts[i+1] if i+1 < len(parts) else ""
            
            # replace break; with nothing if it's at the end
            # actually we don't need to be perfect, just replace 'break;' with '' inside the body
            # because in if-else we don't need break.
            case_body_clean = re.sub(r"\bbreak\s*;", "", case_body)
            
            if case_label.startswith("case"):
                id_match = re.search(r"R\.id\.[a-zA-Z0-9_]+", case_label)
                if id_match:
                    r_id = id_match.group(0)
                    if is_first:
                        new_block.append(f"if ({var_name}.getId() == {r_id}) {{")
                        is_first = False
                    else:
                        new_block.append(f"}} else if ({var_name}.getId() == {r_id}) {{")
                    new_block.append(case_body_clean)
            elif case_label.startswith("default"):
                if is_first:
                    new_block.append(f"{{") # default as first case? rare.
                    is_first = False
                else:
                    new_block.append(f"}} else {{")
                new_block.append(case_body_clean)
            
            i += 2
        
        if new_block:
            new_block.append(f"}}")
            
        return "\n".join(new_block)

    # We need to iteratively apply to handle multiple switches, or nested? 
    # Usually they aren't nested.
    
    # Actually, some might be `switch(id)` instead of `switch(v.getId())`
    pattern2 = r"switch\s*\(\s*([a-zA-Z0-9_]+)\s*\)\s*\{([\s\S]*?)\n\s*\}"
    def replacer2(match):
        var_name = match.group(1)
        body = match.group(2)
        if "case R.id." not in body:
            return match.group(0)
            
        parts = re.split(r"(case\s+R\.id\.[a-zA-Z0-9_]+\s*:|default\s*:)", body)
        if len(parts) <= 1:
            return match.group(0)
            
        new_block = []
        is_first = True
        
        i = 1
        while i < len(parts):
            case_label = parts[i].strip()
            case_body = parts[i+1] if i+1 < len(parts) else ""
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

    content = re.sub(pattern, replacer, content)
    content = re.sub(pattern2, replacer2, content)
    
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
