from data_loader import load_nq_dataset
from preprocess import clean_text
from chunking import chunk_text, extract_title
from chunking import split_sections
from chunking import remove_footer


def build_chunks(data):

    final_chunks = []

    for doc in data:

        doc = clean_text(doc)
        doc = remove_footer(doc)

        title = extract_title(doc)

        sections = split_sections(doc)

        for section, text in sections.items():

            chunks = chunk_text(text)

            for c in chunks:

                enriched_chunk = f"""
Title: {title}
Section: {section}
Content: {c}
"""

                final_chunks.append(enriched_chunk.strip())

    return final_chunks