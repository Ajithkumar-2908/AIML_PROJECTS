import pandas as pd
import pickle
from sklearn.metrics.pairwise import euclidean_distances


cleaned_path = r".\RESTAURANT_CLASSIFICATION\Data\cleaned_data.csv"
new_encoded_path = r".\RESTAURANT_CLASSIFICATION\Data\new\new_encoded_data.csv"
k_means_path = r".\RESTAURANT_CLASSIFICATION\Models\kmeans_model.pkl"
scaler_path = r".\RESTAURANT_CLASSIFICATION\Models\scaler.pkl"
cuisine_encoder_path = r".\RESTAURANT_CLASSIFICATION\Data\cuisine_encoder.pkl"
city_encoder_path = r".\RESTAURANT_CLASSIFICATION\Data\city_encoder.pkl"

# Load saved objects once

encoded_df = pd.read_csv(new_encoded_path)
cleaned_df = pd.read_csv(cleaned_path)

kmeans = pickle.load(open(k_means_path, "rb"))
scaler = pickle.load(open(scaler_path, "rb"))
mlb = pickle.load(open(cuisine_encoder_path, "rb"))
ohe = pickle.load(open(city_encoder_path, "rb"))

def recommend(user_city, user_cuisine_list, user_rating, user_cost, top_n=5):

    # Encode cuisine
    cuisine_encoded = mlb.transform([user_cuisine_list])
    cuisine_df = pd.DataFrame(cuisine_encoded, columns=mlb.classes_)

    # Encode city
    city_encoded = ohe.transform([[user_city]])
    city_df = pd.DataFrame(city_encoded, columns=ohe.get_feature_names_out(['city']))

    # Numeric features
    numeric_df = pd.DataFrame(
        [[user_rating, 0, user_cost]],
        columns=['rating', 'rating_count', 'cost']
    )

    # Combine
    user_vector = pd.concat([numeric_df, cuisine_df, city_df], axis=1)

    # Align columns
    user_vector = user_vector.reindex(columns=encoded_df.columns, fill_value=0)

    # Scale
    user_scaled = scaler.transform(user_vector)

    # Predict cluster
    cluster = kmeans.predict(user_scaled)[0]

    # Filter cluster data
    cluster_data = encoded_df[kmeans.labels_ == cluster]

    # Compute distance
    cluster_scaled = scaler.transform(cluster_data)
    distances = euclidean_distances(user_scaled, cluster_scaled)

    cluster_data = cluster_data.copy()
    cluster_data["distance"] = distances[0]

    top_indices = cluster_data.sort_values("distance").index[:top_n]

    return cleaned_df.loc[top_indices]