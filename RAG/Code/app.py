import streamlit as st
from rag_pipeline import generate_answer

st.title("Wikipedia RAG QA")

question = st.text_input("Ask a question")

if st.button("Ask"):

    answer, sources = generate_answer(question)

    st.subheader("Answer")
    st.write(answer)

    st.subheader("Sources")

    for doc in sources:
        st.write(doc.page_content[:300])
        st.write("---")