from langchain_community.vectorstores import FAISS
from langchain.embeddings import HuggingFaceEmbeddings
from langchain.schema import Document


def build_vector_store(chunks):

    print("Loading embedding model...")

    embeddings = HuggingFaceEmbeddings(
        model_name="sentence-transformers/all-MiniLM-L6-v2"
    )

    print("Converting chunks to documents...")

    docs = [Document(page_content=c) for c in chunks]

    print("Creating FAISS vector store...")

    vector_store = FAISS.from_documents(docs, embeddings)

    print("Saving vector store...")

    vector_store.save_local("..\Vector_Store")

    print("Vector store saved successfully!")