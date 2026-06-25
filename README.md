# Universe Marketplace Platform

A robust, multi-tier campus freelance service marketplace desktop application engineered in Java using **Swing UI** and the **Data Access Object (DAO) Architectural Pattern**. The system connects to a **MySQL relational database layer**, allowing users to list, browse, and track peer-to-peer technical and creative campus services.

The platform's standout innovation is its **Dynamic Seller Loyalty Tier Progression System**, which automatically evaluates and adapts platform commission rates and performance ranks based on historical service point metrics.

---

## 🚀 Key Features

* **Interactive Service Marketplace:** Browsing module featuring visual status grids and custom dark-theme interface components.
* **Service Blueprint Specification:** Modal detail views (`GigDetailsDialog`) rendering detailed contractor matrices, contact strings, and specialized metadata.
* **Order Tracking Lifecycle:** End-to-end booking module (`BookingDetailsDialog`) tracking service statuses, financial evaluations, and live transaction profiles.
* **Dynamic Tier Mechanics:** Multi-table relational lookup calculating seller standings across three ranks (**Bronze**, **Silver**, and **Gold**) with corresponding dynamic platform commission reductions.
* **Defensive Code Architecture:** Fully encapsulated queries using multi-level `LEFT JOIN` structures protected by fallback map parsers to protect the runtime environment against unhandled database null-states.

---

## 🛠️ Technology Stack

* **Frontend UI:** Java Swing Engine (BoxLayout, Grid Layout frameworks)
* **Backend Logic:** Java SE (JDK 11+)
* **Database Engine:** MySQL Enterprise / MariaDB (via phpMyAdmin)
* **Database Connectivity:** JDBC (Java Database Connectivity API)
* **Notification System:** Java Mail API
* **Architecture Pattern:** Data Access Object (DAO) Design Pattern

---




