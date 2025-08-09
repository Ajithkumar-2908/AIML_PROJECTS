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
    database="stocks_db"
)

cursor = connection.cursor()

query = "select * from stock_exchange_details"


stocks_df = pd.read_sql(query, connection)
unique_stocks = stocks_df['ticker'].unique()

yearly_return_df = pd.DataFrame()
yearly_return_df['ticker'] = []
yearly_return_df['yearly_return_perc'] = []
i = 1
for stock in unique_stocks:
    
    stock_df = pd.DataFrame(stocks_df[stocks_df['ticker'] == stock])
    start_day_close = float(stock_df.head(1)['close'])
    end_day_close = float(stock_df.tail(1)['close'])
    yearly_return_perc = ((end_day_close - start_day_close) / start_day_close) * 100
    yearly_return_df.loc[len(yearly_return_df)] = [stock, yearly_return_perc]
    i = i+1

green_stocks = yearly_return_df.sort_values(by='yearly_return_perc', ascending=False)
red_stocks = yearly_return_df.sort_values(by='yearly_return_perc')

green_stocks = green_stocks[green_stocks['yearly_return_perc'] >= 0]
red_stocks = red_stocks[red_stocks['yearly_return_perc'] < 0]

green_stocks_count = len(green_stocks)
red_stocks_count = len(red_stocks)

top_10_green_stocks = green_stocks.head(10)
top_10_red_stocks = red_stocks.head(10)

print(f"Green Stocks Count: {green_stocks_count}") 
print(f"Red Stocks Count: {red_stocks_count}")

print(f"Top 10 Green Stocks Count: {top_10_green_stocks}") 
print(f"Top 10 Red Stocks Count: {top_10_red_stocks}")

query = "select avg(open) as avg_open, avg(close) as avg_close, avg(high) as avg_high, avg(low) as avg_low, avg(volume) as avg_volume from stock_exchange_details"

average_price_volume_df = pd.read_sql(query, connection)

print(f"Average of all stocks is {average_price_volume_df}")