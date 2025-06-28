import random
import pandas as pd
import streamlit as st
import mysql.connector
import matplotlib.pyplot as plt
import numpy as np
import plotly.express as px
import plotly.graph_objects as go
import seaborn as sns



connection = mysql.connector.connect(
    host="localhost",
    user="root",
    password="12345678",
    database="imdb"
)



query = "select * from imdb_movie_details"


df = pd.read_sql(query, connection)
print(df)

st.dataframe(
    df,
    column_config={
        "name": "Name",
        "genre": "Genre",
        "duration": "Duration",
        "rating": st.column_config.NumberColumn(
            "Rating",
            help="Number of stars on IMDB",
            format="%f ⭐"
        ),
        "voting": "Voting"
    },
    hide_index=True,
)

st.subheader("1. Top 10 Movies")

query = "with RankedMovieData as (select name, rating, genre, voting, duration, ROW_NUMBER() OVER (PARTITION BY name ORDER BY rating desc, voting desc) as rn FROM imdb_movie_details WHERE voting > 50000) select name, genre, duration, rating, voting from RankedMovieData where rn = 1 order by rating desc, voting desc LIMIT 10"

top_10_movies_df = pd.read_sql(query, connection)
st.dataframe(
    top_10_movies_df,
    column_config={
        "name": "Name",
        "genre": "Genre",
        "duration": "Duration",
        "rating": st.column_config.NumberColumn(
            "Rating",
            help="Number of stars on IMDB",
            format="%f ⭐"
        ),
        "voting": "Voting"
    },
    hide_index=True,
)

st.subheader("2. Movies Count By Genre")

query = "select count(name) as movie_count, genre from imdb_movie_details group by genre"
genre_movie_count_df = pd.read_sql(query, connection)



st.bar_chart(
    genre_movie_count_df,
    x="genre",
    y="movie_count",
    x_label="Genre",
    y_label="No. Of Movies"
)

st.subheader("3. Average Duration of Movies by Genre")

query = "select avg(duration) as avg_duration, genre from imdb_movie_details group by genre"
genre_avg_duration_df = pd.read_sql(query, connection)

st.bar_chart(
    genre_avg_duration_df,
    x="genre",
    y="avg_duration",
    x_label="Average Duration (in Minutes)",
    y_label="Genre",
    horizontal=True
)

st.subheader("4. Average Voting of Movies by Genre")

query = "select avg(voting) as avg_voting, genre from imdb_movie_details group by genre"
genre_avg_voting_df = pd.read_sql(query, connection)

st.bar_chart(
    genre_avg_voting_df,
    x="genre",
    y="avg_voting",
    x_label="Genre",
    y_label="Voting"
)

st.subheader("5. Rating distribution on Histogram")

query = "select rating from imdb_movie_details"
rating_df = pd.read_sql(query, connection)
arr = rating_df['rating']
fig, ax = plt.subplots()
ax.hist(arr, bins=20)
st.pyplot(fig)


st.subheader("6. Top Rated Movie By Genre")

prev_genre = "Initial"

def rating_highlight(genre):
    global prev_genre
    style = []
    for curr_genre in genre:
        if curr_genre != prev_genre:
            prev_genre = curr_genre
            style.append(f"background-color: green;")
        else:
            prev_genre = curr_genre
            style.append(None)

    return style


query = "select name, rating, genre from imdb_movie_details order by genre asc, rating desc"
rating_sorted_df = pd.read_sql(query, connection)


st.dataframe(
    rating_sorted_df.style.apply(rating_highlight, subset=["genre"]),
    column_config={
        "name": "Name",
        "genre": "Genre",
        "duration": "Duration",
        "rating": st.column_config.NumberColumn(
            "Rating",
            help="Number of stars on IMDB",
            format="%f ⭐"
        ),
        "voting": "Voting"
    },
    hide_index=True,
)


st.subheader("7. Most Popular Genres by Voting")

query = "select sum(voting) as voting_count, genre from imdb_movie_details group by genre"
genre_voting_df = pd.read_sql(query, connection)

fig = px.pie(genre_voting_df, values="voting_count", names='genre')
st.plotly_chart(fig, use_container_width=True)

st.subheader("8. Longest and Shorted Movie")

duration_extremes_query = "select name as Movie, duration as Duration from imdb_movie_details"
duration_extremes_df = pd.read_sql(duration_extremes_query, connection)
duration_extremes_df = duration_extremes_df[duration_extremes_df['Duration']>0]
shortest_movie = duration_extremes_df[duration_extremes_df['Duration'] == duration_extremes_df['Duration'].min()]
longest_movie = duration_extremes_df[duration_extremes_df['Duration'] == duration_extremes_df['Duration'].max()]
shortest_movie['Type'] = 'Shortest Movie'
longest_movie['Type'] = 'Longest Movie'
short_long_movie = pd.concat([shortest_movie,longest_movie], ignore_index= True)
st.dataframe(short_long_movie, hide_index= True, use_container_width= True)

st.subheader("⭐️ 9. Comparison of Average Ratings by Genre")

avg_ratings_query = "select genre as Genre, avg(rating) as Average_Ratings from imdb_movie_details group by genre"
avg_ratings_df = pd.read_sql(avg_ratings_query, connection)
plt.figure(figsize=(6,5))
sns.heatmap(avg_ratings_df.pivot_table(index='Genre', values='Average_Ratings'), annot=True, cmap='YlGnBu', fmt =".1f", linewidths=0, cbar=True)
st.pyplot(plt)

st.subheader("10. Relationship between Ratings and Votings - Scatter Plot")

query = "select rating, voting from imdb_movie_details"
rating_voting_df = pd.read_sql(query, connection)

st.scatter_chart(
    rating_voting_df,
    x="rating",
    y="voting"
)



st.subheader("Choose Filters to pick your Right Movie !!!")

duration_option = st.multiselect('Select Duration', ["< 2 hrs", "2-3 hrs", "> 3 hrs"])
def duration_filter(df):
    if not duration_option:
        return pd.Series([True] * len(df))
    duration_selection = pd.Series([False] * len(df))
    for option in duration_option:
        if option == "< 2 hrs":
            duration_selection |= df['duration'] < 120
        elif option == "2-3 hrs":
            duration_selection |= (df['duration'] >= 120) & (df['duration'] <= 180)
        elif option == ">3 hrs":
            duration_selection |= df['duration'] > 180   
    return duration_selection

rating_option = st.multiselect('Select Rating', ["<5.0", ">5.0", ">6.0", ">7.0", ">8.0", ">9.0"])
def rating_filter(df):
    if not rating_option:
        return pd.Series([True] * len(df))
    rating_selection =pd.Series([False] * len(df))
    for option in rating_option:
        if option == "<5.0":
            rating_selection |= df['rating'] < 5.0
        elif option == ">5.0":
            rating_selection |= df['rating'] > 5.0
        elif option == ">6.0":
            rating_selection |= df['rating'] > 6.0
        elif option == ">7.0":
            rating_selection |= df['rating'] > 7.0
        elif option == ">8.0":
            rating_selection |= df['rating'] > 8.0
        elif option == ">9.0":
            rating_selection |= df['rating'] > 9.0
    return rating_selection

user_count_option = st.multiselect('Select User Count', ["<10000", ">10000", ">25000", ">50000", ">100000"])
def user_count_filter(df):
    if not user_count_option:
        return pd.Series([True] * len(df))
    user_count_selection = pd.Series([False] * len(df))
    for option in user_count_option:
        if option == "<10000":
            user_count_selection |= df['voting'] < 10000
        elif option == ">10000":
            user_count_selection |= df['voting'] > 10000
        elif option == ">25000":
            user_count_selection |= df['voting'] > 25000
        elif option == ">50000":
            user_count_selection |= df['voting'] > 50000
        elif option == ">100000":
            user_count_selection |= df['voting'] > 100000
    return user_count_selection

filtered_query = "select * from imdb_movie_details"
filtered_df = pd.read_sql(filtered_query, connection)
genre_option = st.multiselect('Select Genre', options=list(filtered_df['genre'].unique())) 
genre_choice = filtered_df['genre'].isin(genre_option) if genre_option else pd.Series([True] * len(filtered_df))
duration_choice= duration_filter(filtered_df)
rating_choice = rating_filter(filtered_df)
user_count_choice = user_count_filter(filtered_df)
if not genre_option and not duration_option and not rating_option and not user_count_option:
    st.dataframe(filtered_df, column_config={
         "name": "Name",
         "genre": "Genre",
         "duration": "Duration",
         "rating": st.column_config.NumberColumn(
             "Ratings",
             help="Number of stars on IMDB",
             format="%f ⭐",
         ),
         "voting": "Votings",
     },hide_index=True, use_container_width=True)
else:
    filtered_movies = filtered_df[genre_choice & duration_choice & rating_choice & user_count_choice]
    st.dataframe(filtered_movies,column_config={
         "name": "Name",
         "genre": "Genre",
         "duration": "Duration",
         "rating": st.column_config.NumberColumn(
             "Ratings",
             help="Number of stars on IMDB",
             format="%f ⭐",
         ),
         "voting": "Votings",
     },hide_index=True, use_container_width=True)






