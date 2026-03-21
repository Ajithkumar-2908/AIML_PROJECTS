from build_chunks_pipeline import build_chunks
# from embeddings import load_embedding_model, generate_embeddings
# from vector_store import create_index, save_index, save_chunks
from build_vector_store import build_vector_store
from data_loader import load_nq_dataset

file_path = r"..\Data\v1.0_sample_nq-train-sample.jsonl\nq-train-sample.jsonl"
data = load_nq_dataset(file_path)
chunks = build_chunks(data)

print(f"Total chunks created: {len(chunks)}")
print(f"Sample chunk: {chunks[0]}")

build_vector_store(chunks)

# model = load_embedding_model()

# embeddings = generate_embeddings(model, chunks)

# index = create_index(embeddings)

# save_index(index, "..\Vector_Store\wiki_index.faiss")

# save_chunks(chunks, "..\Vector_Store\wiki_chunks.pkl")

# print("Vector DB built successfully!")
