def extract_title(text):
    """
    Extract title from beginning of Wikipedia article
    """
    return text.split(" - Wikipedia")[0].strip()


def remove_footer(text):
    footer_words = [
        "References",
        "External links",
        "Navigation menu",
        "Retrieved from",
        "Languages",
        "Categories"
    ]

    for word in footer_words:
        if word in text:
            text = text.split(word)[0]

    return text


def split_sections(text):

    section_patterns = [
        " Cast ",
        " Production ",
        " Episodes ",
        " Release ",
        " Reception ",
        " Ratings "
    ]

    sections = {}
    current_section = "Introduction"
    sections[current_section] = ""

    words = text.split()

    buffer = []

    for word in words:

        if word in section_patterns:
            sections[current_section] = " ".join(buffer)
            buffer = []
            current_section = word.strip()
            sections[current_section] = ""
        else:
            buffer.append(word)

    sections[current_section] = " ".join(buffer)

    return sections


def chunk_text(text, chunk_size=120, overlap=30):

    words = text.split()
    chunks = []

    start = 0

    while start < len(words):

        end = start + chunk_size
        chunk = " ".join(words[start:end])

        if len(chunk) > 50:
            chunks.append(chunk)

        start += chunk_size - overlap

    return chunks