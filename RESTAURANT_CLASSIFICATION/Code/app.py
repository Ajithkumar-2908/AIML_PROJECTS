import streamlit as st
import pandas as pd
import pickle
from recommender import recommend

cleaned_path = r".\RESTAURANT_CLASSIFICATION\Data\cleaned_data.csv"
cuisine_encoder_path = r".\RESTAURANT_CLASSIFICATION\Data\cuisine_encoder.pkl"

cleaned_df = pd.read_csv(cleaned_path)
mlb = pickle.load(open(cuisine_encoder_path, "rb"))

st.title("Restaurant Recommendation System")

city = st.selectbox("Select City", cleaned_df["city"].unique())
cuisine = st.multiselect("Select Cuisine", mlb.classes_)
rating = st.slider("Minimum Rating", 1.0, 5.0, 3.5)
cost = st.slider("Approx Cost", 100, 2000, 500)

if st.button("Get Recommendations"):
    results = recommend(city, cuisine, rating, cost)

    st.subheader("Top Recommended Restaurants")
    st.dataframe(results[['name','city','rating','cost','cuisine']])