"""
========================================================================================
SMART CAMPUS EVENT MANAGEMENT AND STUDENT PARTICIPATION ANALYSIS SYSTEM
Course      : 25ITC31 - Foundations of Data Science (VCET IT Dept)
Module      : Data Retrieval, Wrangling, Preprocessing, and Visual Analytics
Script      : analysis.py
========================================================================================
"""

import os
import sqlite3
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Set publication style for figures
plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
sns.set_theme(style="whitegrid", palette="muted")
plt.rcParams['font.sans-serif'] = 'DejaVu Sans'
plt.rcParams['axes.edgecolor'] = '#cccccc'
plt.rcParams['axes.linewidth'] = 0.8

DB_PATH = 'smart_campus.db'
OUTPUT_DIR = os.path.join(os.path.dirname(__file__), 'visualizations')
os.makedirs(OUTPUT_DIR, exist_ok=True)

def get_database_connection():
    """Connects to the SQLite database."""
    if not os.path.exists(DB_PATH):
        raise FileNotFoundError(f"Database file '{DB_PATH}' not found. Please run the schema setup first.")
    return sqlite3.connect(DB_PATH)

def load_and_preprocess_data():
    """
    Unit 2 & 3: Data Retrieval and Wrangling
    Retrieves raw tables and merges into an analytical DataFrame.
    """
    print("\n" + "="*80)
    print("STEP 1: DATABASE RETRIEVAL (25ITC31 Unit 2)")
    print("="*80)
    
    conn = get_database_connection()
    
    query = """
    SELECT 
        r.registration_id,
        r.registration_date,
        r.status AS reg_status,
        s.student_id,
        s.name AS student_name,
        s.department,
        s.year_of_study,
        s.email,
        e.event_id,
        e.event_name,
        e.event_type,
        e.event_date,
        e.venue,
        e.max_participants,
        COALESCE(a.attendance_status, 'Unmarked') AS attendance_status,
        a.feedback_score,
        a.remarks
    FROM registrations r
    JOIN students s ON r.student_id = s.student_id
    JOIN events e ON r.event_id = e.event_id
    LEFT JOIN attendance a ON r.registration_id = a.registration_id;
    """
    
    df = pd.read_sql_query(query, conn)
    conn.close()
    
    print(f"[OK] Successfully loaded {len(df)} records from database.")
    print("\nDataset Snapshot (First 5 Rows):")
    print(df[['student_id', 'student_name', 'department', 'event_name', 'attendance_status', 'feedback_score']].head())
    
    print("\n" + "="*80)
    print("STEP 2: DATA CLEANING & PREPROCESSING (25ITC31 Unit 4)")
    print("="*80)
    
    # Check Missing Values
    print("\nMissing values before cleaning:")
    print(df.isnull().sum())
    
    # Duplicate detection and removal verification
    duplicates = df.duplicated(subset=['student_id', 'event_id']).sum()
    print(f"\nDuplicate student-event pairs found: {duplicates}")
    
    # Feature Engineering & Type Conversion
    df['registration_date'] = pd.to_datetime(df['registration_date'])
    df['event_date'] = pd.to_datetime(df['event_date'])
    df['is_present'] = df['attendance_status'].apply(lambda x: 1 if x == 'Present' else 0)
    
    # Feedback score handling: Absent students do not provide feedback
    df['has_feedback'] = df['feedback_score'].notnull().astype(int)
    
    # Export cleaned analytical dataset
    clean_csv_path = os.path.join(os.path.dirname(__file__), 'preprocessed_participation_data.csv')
    df.to_csv(clean_csv_path, index=False)
    print(f"\n[OK] Cleaned dataset saved to: {clean_csv_path}")
    
    # Summary Statistics (Unit 1: Mean, Median, Standard Deviation)
    print("\nSummary Statistics for Numerical Variables:")
    print(df[['year_of_study', 'max_participants', 'is_present', 'feedback_score']].describe())
    
    return df

def generate_visualizations(df):
    """
    Unit 5: Exploratory Data Analysis & Visualizations
    Produces high quality charts using Matplotlib and Seaborn.
    """
    print("\n" + "="*80)
    print("STEP 3: GENERATING DATA VISUALIZATIONS (25ITC31 Unit 5)")
    print("="*80)
    
    # -------------------------------------------------------------------------
    # Chart 1: Department-Wise Participation Distribution (Grouped Bar Plot)
    # -------------------------------------------------------------------------
    plt.figure(figsize=(10, 6))
    dept_summary = df.groupby('department').agg(
        Total_Registered=('registration_id', 'count'),
        Attended=('is_present', 'sum')
    ).reset_index()
    dept_melted = pd.melt(dept_summary, id_vars=['department'], 
                          value_vars=['Total_Registered', 'Attended'],
                          var_name='Metric', value_name='Count')
    
    ax = sns.barplot(data=dept_melted, x='department', y='Count', hue='Metric', palette=['#1f77b4', '#2ca02c'])
    plt.title('Campus Event Participation by Department', fontsize=14, weight='bold', pad=15)
    plt.xlabel('Academic Department', fontsize=11, labelpad=10)
    plt.ylabel('Student Count', fontsize=11)
    plt.xticks(rotation=20, ha='right')
    for p in ax.patches:
        height = p.get_height()
        if not np.isnan(height) and height > 0:
            ax.annotate(f'{int(height)}',
                        (p.get_x() + p.get_width() / 2., height),
                        ha='center', va='bottom', fontsize=10, xytext=(0, 3),
                        textcoords='offset points')
    plt.tight_layout()
    chart1_path = os.path.join(OUTPUT_DIR, '01_department_participation_barchart.png')
    plt.savefig(chart1_path, dpi=300)
    plt.close()
    print(f"  [SAVED] Chart 1: {chart1_path}")

    # -------------------------------------------------------------------------
    # Chart 2: Event Attendance Comparison (Capacity vs Registered vs Present)
    # -------------------------------------------------------------------------
    plt.figure(figsize=(11, 6))
    event_summary = df.groupby('event_name').agg(
        Capacity=('max_participants', 'first'),
        Registered=('registration_id', 'count'),
        Present=('is_present', 'sum')
    ).reset_index()
    
    event_melted = pd.melt(event_summary, id_vars=['event_name'],
                           value_vars=['Capacity', 'Registered', 'Present'],
                           var_name='Category', value_name='Students')
    
    ax2 = sns.barplot(data=event_melted, x='event_name', y='Students', hue='Category',
                      palette=['#aec7e8', '#ff7f0e', '#2ca02c'])
    plt.title('Event Capacity vs. Registrations vs. Actual Attendance', fontsize=14, weight='bold', pad=15)
    plt.xlabel('Campus Event', fontsize=11, labelpad=10)
    plt.ylabel('Number of Students', fontsize=11)
    plt.xticks(rotation=25, ha='right')
    plt.legend(title='Metric')
    plt.tight_layout()
    chart2_path = os.path.join(OUTPUT_DIR, '02_event_attendance_comparison.png')
    plt.savefig(chart2_path, dpi=300)
    plt.close()
    print(f"  [SAVED] Chart 2: {chart2_path}")

    # -------------------------------------------------------------------------
    # Chart 3: Event Type Share (Pie Chart)
    # -------------------------------------------------------------------------
    plt.figure(figsize=(8, 8))
    type_counts = df['event_type'].value_counts()
    colors = ['#4c72b0', '#55a868', '#c44e52', '#8172b3']
    plt.pie(type_counts, labels=type_counts.index, autopct='%1.1f%%',
            startangle=140, colors=colors, explode=[0.05]*len(type_counts),
            textprops={'fontsize': 11, 'weight': 'bold'},
            wedgeprops={'edgecolor': 'white', 'linewidth': 2})
    plt.title('Proportion of Registrations Across Event Categories', fontsize=14, weight='bold', pad=20)
    plt.tight_layout()
    chart3_path = os.path.join(OUTPUT_DIR, '03_event_type_distribution_piechart.png')
    plt.savefig(chart3_path, dpi=300)
    plt.close()
    print(f"  [SAVED] Chart 3: {chart3_path}")

    # -------------------------------------------------------------------------
    # Chart 4: Year of Study Participation (Count Plot)
    # -------------------------------------------------------------------------
    plt.figure(figsize=(9, 5))
    ax4 = sns.countplot(data=df, x='year_of_study', hue='attendance_status', palette={'Present': '#2ca02c', 'Absent': '#d62728'})
    plt.title('Student Event Participation by Year of Study', fontsize=14, weight='bold', pad=15)
    plt.xlabel('Academic Year of Study', fontsize=11)
    plt.ylabel('Total Registrations', fontsize=11)
    plt.xticks([0, 1, 2, 3], ['Year 1', 'Year 2', 'Year 3', 'Year 4'])
    plt.legend(title='Status')
    for p in ax4.patches:
        height = p.get_height()
        if not np.isnan(height) and height > 0:
            ax4.annotate(f'{int(height)}',
                         (p.get_x() + p.get_width() / 2., height),
                         ha='center', va='bottom', fontsize=10, xytext=(0, 3),
                         textcoords='offset points')
    plt.tight_layout()
    chart4_path = os.path.join(OUTPUT_DIR, '04_year_wise_participation_countplot.png')
    plt.savefig(chart4_path, dpi=300)
    plt.close()
    print(f"  [SAVED] Chart 4: {chart4_path}")

    # -------------------------------------------------------------------------
    # Chart 5: Feedback Rating Distribution across Event Types (Box Plot)
    # -------------------------------------------------------------------------
    plt.figure(figsize=(9, 6))
    present_df = df[df['attendance_status'] == 'Present'].dropna(subset=['feedback_score'])
    sns.boxplot(data=present_df, x='event_type', y='feedback_score', palette='Set2')
    sns.stripplot(data=present_df, x='event_type', y='feedback_score', color='black', alpha=0.5, jitter=0.2, size=7)
    plt.title('Student Feedback Ratings Distribution by Event Type', fontsize=14, weight='bold', pad=15)
    plt.xlabel('Event Type', fontsize=11)
    plt.ylabel('Feedback Score (1 - 5)', fontsize=11)
    plt.ylim(0.5, 5.5)
    plt.tight_layout()
    chart5_path = os.path.join(OUTPUT_DIR, '05_feedback_rating_boxplot.png')
    plt.savefig(chart5_path, dpi=300)
    plt.close()
    print(f"  [SAVED] Chart 5: {chart5_path}")

    # -------------------------------------------------------------------------
    # Chart 6: Correlation Matrix Heatmap
    # -------------------------------------------------------------------------
    plt.figure(figsize=(8, 6))
    corr_df = df[['year_of_study', 'max_participants', 'is_present', 'feedback_score']].copy()
    corr_df['feedback_score'] = corr_df['feedback_score'].fillna(0)
    corr_matrix = corr_df.corr()
    
    sns.heatmap(corr_matrix, annot=True, cmap='Blues', fmt='.2f', square=True,
                linewidths=1.5, cbar_kws={'shrink': 0.8})
    plt.title('Feature Correlation Matrix (EDA)', fontsize=14, weight='bold', pad=15)
    plt.tight_layout()
    chart6_path = os.path.join(OUTPUT_DIR, '06_participation_correlation_heatmap.png')
    plt.savefig(chart6_path, dpi=300)
    plt.close()
    print(f"  [SAVED] Chart 6: {chart6_path}")

    print("\n" + "="*80)
    print("ALL VISUALIZATIONS GENERATED AND SAVED TO: data_analysis/visualizations/")
    print("="*80 + "\n")

if __name__ == '__main__':
    dataframe = load_and_preprocess_data()
    generate_visualizations(dataframe)
