import os

from langchain.chat_models import ChatOpenAI
from langchain.schema import HumanMessage

from flask import Flask, jsonify, request
from gevent import pywsgi

app = Flask(__name__)

'''utils.py
项目python版本>3.7
项目依赖库 langchain openai flask
'''

'''utils.py
chat = ChatOpenAI(temperature=0)

template = "You are a helpful assistant that translates {input_language} to {output_language}."
system_message_prompt = SystemMessagePromptTemplate.from_template(template)
human_template = "{text}"
human_message_prompt = HumanMessagePromptTemplate.from_template(human_template)

chat_prompt = ChatPromptTemplate.from_messages([system_message_prompt, human_message_prompt])

# 赋值
chat(chat_prompt.format_prompt(input_language="English", output_language="French", text="I love programming.").to_messages())
# -> AIMessage(content="J'aime programmer.", additional_kwargs={})
'''

def get_templete(style, keywords):
    if style == "article":
        text = "请根据关键词：" + keywords + "生成一篇200字左右的中文文章。"
    elif style == "poem":
        text = "请根据关键词：" + keywords + "生成一首7言诗。"
    elif style == "couplet":
        text = "请根据关键词：" + keywords + "生成一首对联。"
    elif style == "redbook":
        text = "请根据关键词：" + keywords + "生成一个小红书风格的帖子,加入表情来丰富内容。"
    return text


def generate_post(text):
    # TODO: enter your own key and api address
    os.environ['OPENAI_API_BASE'] = "FAKE_ADDRESS"
    os.environ["OPENAI_API_KEY"] = "FAKE_KEY"
    chat = ChatOpenAI(temperature=0.7, max_tokens=2048)
    # text = "请根据关键词：" + keywords + "生成一篇200字左右的中文文章。"
    reply = chat([HumanMessage(content=text)])   
    article = reply.content
    return article

@app.route('/api/article', methods=['POST'])
def post_article():
    data = request.get_json()
    keywords = data['keywords']
    style = data['style']
    text = get_templete(style, keywords)
    article = generate_post(text)
    data['article'] = article
    return jsonify(data)

if __name__ == '__main__':
    server = pywsgi.WSGIServer(('127.0.0.6', 9091), app)
    server.serve_forever()
    app.run(debug=False)