import json
from bs4 import BeautifulSoup
import re

def load_nq_dataset(file_path):
    data = []
    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            record = json.loads(line)
            html_content = record.get("document_html", "")

            # Parse HTML and extract text
            soup = BeautifulSoup(html_content, "html.parser")
            plain_text = soup.get_text(separator=" ", strip=True)
            data.append(plain_text)
    
    return data