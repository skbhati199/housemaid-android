import subprocess
import json

projects = ["authsetu", "learningspacepro-dev", "nearbybook", "payliowise", "rydo-dev-ecf0d", "rydo-prod-e99d7", "rydogrocery"]
package_name = "com.housemaid"

for project in projects:
    print(f"Checking project: {project}")
    try:
        result = subprocess.run("firebase apps:list ANDROID --project " + project + " --json", capture_output=True, text=True, shell=True)
        if result.returncode == 0:
            data = json.loads(result.stdout)
            for app in data.get("result", []):
                if app.get("packageName") == package_name:
                    print(f"FOUND in project: {project}")
                    print(json.dumps(app, indent=2))
    except Exception as e:
        print(f"Error checking {project}: {e}")
