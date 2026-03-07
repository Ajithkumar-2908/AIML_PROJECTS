import streamlit as st
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model
from PIL import Image

# Load the trained model
model = load_model(r"..\MODEL\best_fish_model.h5")

# Class names (same order as training folders)
class_names = [
    "animal fish",
    "animal fish bass",
    "fish sea_food black_sea_sprat",
    "fish sea_food gilt_head_bream",
    "fish sea_food hourse_mackerel",
    "fish sea_food red_mullet",
    "fish sea_food red_sea_bream",
    "fish sea_food sea_bass",
    "fish sea_food shrimp",
    "fish sea_food striped_red_mullet",
    "fish sea_food trout"
]

# Page title
st.title("🐟 Fish Image Classification App")

st.write("Upload a fish image and the model will predict its category.")

# Upload image
uploaded_file = st.file_uploader("Upload Fish Image", type=["jpg","jpeg","png"])

if uploaded_file is not None:

    # Open image
    image = Image.open(uploaded_file).convert("RGB")

    # Show image
    st.image(image, caption="Uploaded Image", use_container_width=True)

    # Resize image
    img = image.resize((224,224))

    # Convert to array
    img_array = np.array(img)

    # Normalize
    img_array = img_array / 255.0

    # Add batch dimension
    img_array = np.expand_dims(img_array, axis=0)

    # Prediction
    predictions = model.predict(img_array)

    predicted_class_index = np.argmax(predictions)

    predicted_class = class_names[predicted_class_index]

    confidence = np.max(predictions) * 100

    # Display results
    st.subheader("Prediction Result")

    st.write(f"**Fish Category:** {predicted_class}")

    st.write(f"**Confidence:** {confidence:.2f}%")

    # Show probability distribution
    st.subheader("Class Probabilities")

    for i, cls in enumerate(class_names):
        st.write(f"{cls} : {predictions[0][i]*100:.2f}%")