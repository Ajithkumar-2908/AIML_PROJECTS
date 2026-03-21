from langchain_community.vectorstores import FAISS
from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain.prompts import PromptTemplate
from langchain_ollama import OllamaLLM

from reranker import rerank_documents


def load_vector_store():

    VECTOR_DB_PATH = "..\Vector_Store"

    embeddings = HuggingFaceEmbeddings(
        model_name="sentence-transformers/all-MiniLM-L6-v2"
    )

    return FAISS.load_local(
        VECTOR_DB_PATH,
        embeddings,
        allow_dangerous_deserialization=True
    )


def generate_answer(question):

    vector_store = load_vector_store()

    retriever = vector_store.as_retriever(search_kwargs={"k": 10})

    docs = retriever.invoke(question)

    # 🔥 LangChain retrieval + custom reranking
    reranked_docs = rerank_documents(question, docs, top_k=3)

    context = "\n\n".join(doc.page_content for doc in reranked_docs)

    prompt = PromptTemplate(
        template="""
You are a helpful assistant. 
Use the following context to answer the question.
Do not make up information. If the answer is not in the context, say you don't know.

Context:
{context}

Question:
{question}

Answer:
""",
        input_variables=["context", "question"],
    )

    llm = OllamaLLM(model="llama3.2:1b")

    chain = prompt | llm

    response = chain.invoke({
        "context": context,
        "question": question
    })

    return response, reranked_docs