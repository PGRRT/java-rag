METRYKI I SCENARIUSZE TESTOWE: MEDICAL CHATBOT PERFORMANCE EVALUATION

1. Czas odpowiedzi (Latency)
Analiza opóźnień zostanie rozbita na poszczególne składowe, aby precyzyzyjnie wskazać narzut orkiestracji:
* Total Response Latency: Pomiar całkowitego czasu od wysłania zapytania przez użytkownika do otrzymania zagregowanych danych.
* Internal Aggregation Latency: Czas poświęcony wyłącznie na scalanie i sortowanie danych w pamięci (porównanie wydajności Javy 21 i silnika PostgreSQL FDW).
* I/O Wait Latency: Monitorowanie czasu, w którym system oczekuje na pakiety z odległych baz regionalnych.
* Analiza Percentyli: Wyznaczenie wartości $p95$ oraz $p99$, aby ocenić stabilność systemu w gorszych warunkach sieciowych.

2. Przepustowość (Throughput)
Badanie zdolności systemu do obsługi ruchu masowego:
* Requests Per Second ($RPS$): Wyznaczenie maksymalnej liczby żądań na sekundę obsługiwanych przed degradacją wydajności systemu.
* Porównanie wydajnościowe: Zestawienie przepustowości mechanizmu federacji PostgreSQL FDW oraz autorskiego Middleware opartego na Wątkach Wirtualnych.

3. Efektywność utylizacji zasobów (Resource Efficiency)
Weryfikacja kosztu obsługi współbieżności:
* Zużycie pamięci RAM: Profilowanie pamięci przy tysiącach jednoczesnych połączeń – weryfikacja lekkości Project Loom.
* Obciążenie procesora (CPU): Monitorowanie narzutu procesowego przy intensywnej orkiestracji zapytań rozproszonych w warstwie aplikacji kontra warstwa bazy danych.

4. Odporność na awarie i błędy (Fault Tolerance)
Badanie zachowania systemu w warunkach krytycznych:
* Symulacja awarii: Całkowita niedostępność węzła bazy danych w innym regionie (np. USA).
* Mechanizmy zabezpieczające: Testowanie skuteczności Timeouts oraz Circuit Breakers w warstwie Javy.
* Network Jitter: Badanie wpływu nagłych zmian opóźnień sieciowych na spójność agregowanych danych.

5. Skalowalność (Scalability)
Ocena elastyczności architektury Hub & Spoke:
* Skalowanie pionowe i poziome: Testowanie zachowania systemu przy gwałtownym przyroście sesji użytkowników (skok ze 100 do 1000 współbieżnych połączeń).
* Wzorzec Scatter-Gather: Ocena wydajności przy zwiększaniu liczby odpytywanych regionów danych (np. dodanie trzeciej bazy w Azji).
