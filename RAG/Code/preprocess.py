import re

def clean_text(text):
    text = text.replace("\xa0", " ")

    # remove citations like [1]
    text = re.sub(r"\[\d+\]", "", text)

    # remove extra spaces
    text = re.sub(r"\s+", " ", text)

    return text.strip()