from sentence_transformers import CrossEncoder

# load once
reranker = CrossEncoder("cross-encoder/ms-marco-MiniLM-L-6-v2")


def rerank_documents(query, docs, top_k=3):

    pairs = [[query, doc.page_content] for doc in docs]

    scores = reranker.predict(pairs)

    scored_docs = list(zip(docs, scores))

    scored_docs.sort(key=lambda x: x[1], reverse=True)

    return [doc for doc, score in scored_docs[:top_k]]