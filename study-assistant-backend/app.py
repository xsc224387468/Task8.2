# app.py
from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
import json
from requests.exceptions import RequestException
import time
from datetime import datetime
import re

app = Flask(__name__)
CORS(app)

# General function: interact with Ollama local Llama2
def query_ollama(prompt, timeout=120):  # Increased timeout to 120 seconds
    try:
        response = requests.post(
            "http://localhost:11434/api/generate",
            json={
                "model": "llama2",
                "prompt": prompt,
                "stream": False
            },
            timeout=timeout
        )
        response.raise_for_status()
        return response.json().get("response", "")
    except RequestException as e:
        print(f"Error querying Ollama: {str(e)}")
        return "Sorry, the service is temporarily unavailable. Please try again later."

@app.route("/", methods=["GET"])
def index():
    return jsonify({"status": "API is running"})

@app.route("/api/summarize", methods=["POST"])
def summarize():
    try:
        content = request.json.get("text", "")
        if not content:
            return jsonify({"error": "Please provide content to summarize"}), 400
            
        prompt = f"Please summarize the following study notes in concise language:\n{content}"
        summary = query_ollama(prompt)
        return jsonify({"summary": summary})
    except Exception as e:
        print(f"Error in summarize: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route("/api/qa", methods=["POST"])
def qa():
    try:
        context = request.json.get("context", "")
        question = request.json.get("question", "")
        
        if not question:
            return jsonify({"error": "Please provide a question"}), 400
            
        prompt = f"Based on the following content, please answer the question:\n{context}\n\nQuestion: {question}"
        answer = query_ollama(prompt)
        return jsonify({"answer": answer})
    except Exception as e:
        print(f"Error in qa: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route("/api/study-plan", methods=["POST"])
def generate_study_plan():
    try:
        # Get request parameters
        data = request.json
        learning_goal = data.get("learningGoal", "")
        courses = data.get("courses", "")
        daily_time = data.get("dailyTime", "")
        deadline = data.get("deadline", "")

        # Validate required parameters
        if not all([learning_goal, courses, daily_time, deadline]):
            return jsonify({"error": "Please provide all required information"}), 400

        # 拆分 deadline 为 start_date 和 end_date
        if '~' in deadline:
            start_date, end_date = [d.strip() for d in deadline.split('~', 1)]
        else:
            start_date = end_date = deadline

        # Build prompt (ENGLISH, force strict JSON output, dailyTasks must be date mapping, include learningGoal/courses)
        prompt = f"""
You are an expert study assistant. Based on the following user information, generate a detailed study plan and output ONLY valid JSON (no notes, no explanations, no markdown, no extra text).

User Info:
Learning Goal: {learning_goal}
Courses: {courses}
Daily Available Study Time (hours): {daily_time}
Start Date: {start_date}
End Date: {end_date}

The JSON must include these fields:
- "learningGoal": the user's learning goal (string, same as above)
- "courses": the user's courses (string, same as above)
- "time_distribution": a mapping of the user's actual course names (from the 'Courses' field above) to the number of study hours (e.g., {{"Mobile Application Development": 10, "Llama 2": 15}})
- "dailyTasks": a mapping of each date (in yyyy-MM-dd format, from Start Date to End Date, inclusive) to the specific study tasks for that day, as a string. Each day's task should reference the actual course names and be tailored to the user's goals.
- "explanation": a paragraph explaining why this plan is arranged this way and what the benefits are, tailored to the user's input.
- "resources": a list of recommended websites, books, or other resources for the user's learning goals and courses.

IMPORTANT:
- Use the actual course names provided by the user in the 'Courses' field.
- "dailyTasks" must be a JSON object mapping each date (yyyy-MM-dd) to a string.
- Do NOT include any notes, explanations, or extra text outside the JSON.
- Do NOT use markdown formatting.
- Output ONLY valid JSON.
"""

        # Call Llama 2 to generate plan
        plan = query_ollama(prompt)
        print("Llama2 raw output:", plan)  # 调试打印

        # 尝试只提取第一个 JSON 对象
        try:
            match = re.search(r'\{[\s\S]*\}', plan)
            if match:
                json_str = match.group(0)
                structured_plan = json.loads(json_str)
            else:
                raise ValueError("No JSON object found in Llama2 output.")
        except Exception as e:
            print("Failed to parse Llama2 output as JSON:", e)
            structured_plan = {"rawPlan": plan}

        return jsonify({
            "plan": structured_plan,
            "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        })

    except Exception as e:
        print(f"Error generating study plan: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route("/api/health", methods=["GET"])
def health_check():
    try:
        # Check if Ollama is running
        ollama_response = requests.get("http://localhost:11434/api/version", timeout=5)
        ollama_version = ollama_response.json().get("version", "unknown")
        
        return jsonify({
            "status": "healthy",
            "ollama_version": ollama_version,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S")
        })
    except Exception as e:
        print(f"Health check failed: {str(e)}")
        return jsonify({
            "status": "unhealthy",
            "error": str(e),
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S")
        }), 500

if __name__ == "__main__":
    print("Starting Flask server...")
    print("Make sure Ollama is running on http://localhost:11434")
    app.run(host="0.0.0.0", port=5000, debug=True)
