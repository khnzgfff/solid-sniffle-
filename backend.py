from flask import Flask, request, jsonify
import openai
import random
import os

app = Flask(__name__)

# ضع مفتاح OpenAI هنا (أو استخدم DeepSeek)
openai.api_key = os.environ.get("OPENAI_API_KEY", "your-key-here")

PERSONAS = {
    "professional": {
        "age": 35,
        "job": "Software Engineer",
        "interests": ["technology", "business", "gaming"],
        "income": "high"
    },
    "student": {
        "age": 22,
        "job": "College Student",
        "interests": ["social media", "music", "sports"],
        "income": "low"
    },
    "parent": {
        "age": 45,
        "job": "Small Business Owner",
        "interests": ["family", "home improvement", "travel"],
        "income": "medium"
    }
}

@app.route('/answer', methods=['POST'])
def answer_question():
    data = request.json
    question = data.get('question')
    options = data.get('options', [])
    persona_type = data.get('persona', 'professional')
    
    persona = PERSONAS.get(persona_type, PERSONAS['professional'])
    
    prompt = f"""You are a real person with these traits:
- Age: {persona['age']}
- Job: {persona['job']}
- Interests: {', '.join(persona['interests'])}
- Income: {persona['income']}

Answer this survey question naturally and briefly. Do NOT sound like an AI.
Question: {question}
"""
    if options:
        prompt += f"\nOptions: {', '.join(options)}"
    
    try:
        response = openai.ChatCompletion.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": "You are a real human answering a survey. Be natural."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.8,
            max_tokens=50
        )
        answer = response.choices[0].message.content
        
        # إذا كان هناك خيارات، ابحث عن الأقرب
        if options:
            for opt in options:
                if opt.lower() in answer.lower():
                    return jsonify({"answer": opt})
            return jsonify({"answer": random.choice(options)})
        
        return jsonify({"answer": answer})
    except Exception as e:
        return jsonify({"answer": "Yes"}), 200

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ok"})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
