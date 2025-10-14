Welcome to my RAG Project !!!


Step 1: Create a new Virtual Environment.
Command: python -m venv venv

Step 2: Activate the Virtual Environment "venv"
Command: ./venv/Scripts/Activate
Note: If the script execution is restricted by Windows, update the execution policy by running the below command.
Command: Set-ExecutionPolicy Unrestricted

Step 3: Install the packages.
Command: pip install .\requirements.txt

Step 4: Install OLLAMA from ollama.com and run the installation setup.

Step 5: Once OLLAMA is installed, open a command prompt and type the below command.
Command: ollama
Note: ollama command should be recognized now.

Step 6: Check the available models from "ollama.com/library"

Step 7: Pull llama3.2 model in your local machine. Suitable for machines with any GPU.
Command: ollama pull llama3.2

Step 8: Pull an embedding model.
Command: ollama pull mxbai-embed-large

Command to list the available models in your machine: ollama list