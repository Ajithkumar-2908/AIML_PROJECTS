import pandas as pd
import matplotlib.pyplot as plt
import streamlit as st
import seaborn as sns

def plot_top_volatile_stocks(stocks_df):
    # Sort data
    df = stocks_df.sort_values(by=['ticker', 'date']).copy()
    
    # Daily returns
    df['daily_return'] = df.groupby('ticker')['close'].pct_change()
    
    # Volatility calculation
    volatility_df = (
        df.groupby('ticker')['daily_return']
        .std()
        .reset_index()
        .rename(columns={'daily_return': 'volatility'})
    )
    
    # Top 10 volatile stocks
    top_10_volatile = volatility_df.sort_values(
        by='volatility', ascending=False
    ).head(10)

    # Plot
    fig, ax = plt.subplots(figsize=(12,6))
    ax.bar(top_10_volatile['ticker'], top_10_volatile['volatility'])
    
    ax.set_title("Top 10 Most Volatile Stocks")
    ax.set_xlabel("Stock Ticker")
    ax.set_ylabel("Volatility (Std Dev of Daily Returns)")
    plt.xticks(rotation=45)
    ax.grid(axis='y')

    # Show in Streamlit
    st.pyplot(fig)

    # Optional: show table
    st.subheader("Top 10 Volatile Stocks Data")
    st.dataframe(top_10_volatile)



def plot_cumulative_return_top5(stocks_df):
    # Step 1: Sort data
    df = stocks_df.sort_values(by=['ticker', 'date']).copy()
    
    # Step 2: Daily returns
    df['daily_return'] = df.groupby('ticker')['close'].pct_change()
    
    # Step 3: Cumulative return
    # Formula: (1 + r1) * (1 + r2) * ... - 1
    df['cumulative_return'] = (
        (1 + df['daily_return'])
        .groupby(df['ticker'])
        .cumprod() - 1
    )
    
    # Step 4: Get final cumulative return per stock
    final_returns = (
        df.groupby('ticker')['cumulative_return']
        .last()
        .reset_index()
    )
    
    # Top 5 performing stocks
    top5 = final_returns.sort_values(
        by='cumulative_return', ascending=False
    ).head(5)
    
    top5_tickers = top5['ticker'].tolist()
    
    # Filter data for top 5
    top5_df = df[df['ticker'].isin(top5_tickers)]
    
    # Step 5: Plot
    fig, ax = plt.subplots(figsize=(12,6))
    
    for ticker in top5_tickers:
        stock_data = top5_df[top5_df['ticker'] == ticker]
        ax.plot(
            stock_data['date'],
            stock_data['cumulative_return'],
            label=ticker
        )
    
    ax.set_title("Cumulative Return - Top 5 Performing Stocks")
    ax.set_xlabel("Date")
    ax.set_ylabel("Cumulative Return")
    ax.legend()
    ax.grid()
    
    plt.xticks(rotation=45)
    
    # Show in Streamlit
    st.pyplot(fig)
    
    # Optional: Show summary table
    st.subheader("Top 5 Performing Stocks")
    st.dataframe(top5)


import pandas as pd
import matplotlib.pyplot as plt
import streamlit as st

def plot_sector_performance(stocks_df, sector_file_path):
    
    # Step 1: Load sector data
    sector_df = pd.read_csv(sector_file_path)
    
    # Step 2: Extract ticker symbol from "Symbol" column
    sector_df['ticker'] = sector_df['Symbol'].apply(
        lambda x: x.split(":")[-1].strip()
    )
    
    # Keep only required columns
    sector_df = sector_df[['ticker', 'sector']]
    
    # Step 3: Prepare stock data
    df = stocks_df.sort_values(by=['ticker', 'date']).copy()
    
    # Step 4: Compute yearly return per stock
    yearly_returns = df.groupby('ticker').agg(
        start_price=('close', 'first'),
        end_price=('close', 'last')
    ).reset_index()
    
    yearly_returns['yearly_return'] = (
        (yearly_returns['end_price'] - yearly_returns['start_price']) 
        / yearly_returns['start_price']
    )
    
    # Step 5: Merge with sector data
    merged_df = pd.merge(
        yearly_returns,
        sector_df,
        on='ticker',
        how='inner'
    )
    
    # Step 6: Sector-wise average return
    sector_performance = (
        merged_df.groupby('sector')['yearly_return']
        .mean()
        .reset_index()
        .sort_values(by='yearly_return', ascending=False)
    )
    
    # Step 7: Plot
    fig, ax = plt.subplots(figsize=(12,6))
    
    ax.bar(
        sector_performance['sector'],
        sector_performance['yearly_return']
    )
    
    ax.set_title("Average Yearly Return by Sector")
    ax.set_xlabel("Sector")
    ax.set_ylabel("Average Yearly Return")
    
    plt.xticks(rotation=45)
    ax.grid(axis='y')
    
    # Show in Streamlit
    st.pyplot(fig)
    
    # Optional: Show data
    st.subheader("Sector Performance Table")
    st.dataframe(sector_performance)


def plot_stock_correlation_heatmap(stocks_df):
    
    # Step 1: Prepare data
    df = stocks_df.sort_values(by=['date']).copy()
    
    # Pivot: rows = date, columns = ticker
    pivot_df = df.pivot(index='date', columns='ticker', values='close')
    
    # Step 2: Compute daily returns
    returns_df = pivot_df.pct_change()
    
    # Step 3: Correlation matrix
    corr_matrix = returns_df.corr()
    
    # Step 4: Plot heatmap
    fig, ax = plt.subplots(figsize=(12,8))
    
    sns.heatmap(
        corr_matrix,
        annot=True,          # show correlation values
        fmt=".2f",
        cmap="coolwarm",
        center=0,
        ax=ax
    )
    
    ax.set_title("Stock Price Correlation Heatmap")
    
    # Show in Streamlit
    st.pyplot(fig)
    
    # Optional: Show raw matrix
    st.subheader("Correlation Matrix")
    st.dataframe(corr_matrix)



def plot_monthly_gainers_losers(stocks_df):
    
    # Step 1: Prepare data
    df = stocks_df.copy()
    df['date'] = pd.to_datetime(df['date'])
    
    # Extract month (YYYY-MM format)
    df['year_month'] = df['date'].dt.to_period('M')
    
    # Step 2: Calculate monthly returns
    monthly_returns = df.sort_values(['ticker', 'date']).groupby(
        ['ticker', 'year_month']
    ).agg(
        start_price=('close', 'first'),
        end_price=('close', 'last')
    ).reset_index()
    
    monthly_returns['monthly_return'] = (
        (monthly_returns['end_price'] - monthly_returns['start_price']) 
        / monthly_returns['start_price']
    )
    
    # Convert period to string for plotting
    monthly_returns['year_month'] = monthly_returns['year_month'].astype(str)
    
    # Step 3: Get unique months
    months = sorted(monthly_returns['year_month'].unique())
    
    st.subheader("📊 Top 5 Gainers & Losers (Month-wise)")
    
    # Step 4: Create charts (2 columns layout)
    cols = st.columns(2)
    
    for i, month in enumerate(months):
        month_df = monthly_returns[monthly_returns['year_month'] == month]
        
        # Top 5 gainers
        gainers = month_df.sort_values(
            by='monthly_return', ascending=False
        ).head(5)
        
        # Top 5 losers
        losers = month_df.sort_values(
            by='monthly_return', ascending=True
        ).head(5)
        
        # Combine for plotting
        combined = pd.concat([gainers, losers])
        
        # Plot
        fig, ax = plt.subplots(figsize=(8,4))
        
        ax.bar(
            combined['ticker'],
            combined['monthly_return']
        )
        
        ax.set_title(f"{month} - Top Gainers & Losers")
        ax.set_xlabel("Ticker")
        ax.set_ylabel("Monthly Return")
        plt.xticks(rotation=45)
        ax.axhline(0)  # zero line for reference
        
        # Alternate columns (2 per row)
        with cols[i % 2]:
            st.pyplot(fig)