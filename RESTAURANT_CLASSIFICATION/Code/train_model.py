import pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
import pickle

# Load encoded data
new_encoded_path = r".\RESTAURANT_CLASSIFICATION\Data\new\new_encoded_data.csv"
df = pd.read_csv(new_encoded_path)

# Scale
scaler = StandardScaler()
scaled_data = scaler.fit_transform(df)

# Train KMeans
kmeans = KMeans(n_clusters=8, random_state=42)
kmeans.fit(scaled_data)

# Save model + scaler
k_means_path = r".\RESTAURANT_CLASSIFICATION\Models\kmeans_model.pkl"
scaler_path = r".\RESTAURANT_CLASSIFICATION\Models\scaler.pkl"
pickle.dump(kmeans, open(k_means_path, "wb"))
pickle.dump(scaler, open(scaler_path, "wb"))

print("✅ Model trained and saved")