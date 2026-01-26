Dlaczego PostgreSQL, a nie Cassandra?

Dla danych medycznych nie mozemy sobie pozwolic na brak jakis danych!

Fundament Badawczy: Celem pracy jest ewaluacja mechanizmu PostgreSQL FDW jako punktu odniesienia (Baseline) dla Javy 21. Wybór innej bazy uniemożliwiłby realizację głównego zadania badawczego.

Model Federacyjny: Tylko PostgreSQL posiada dojrzały mechanizm federacji (Foreign Data Wrapper) oparty na standardzie SQL/MED, co pozwala na bezpośrednie porównanie "ciężkiej" federacji DB z "lekką" orkiestracją w Javie.

Rygor Medyczny (ACID): Dane medyczne wymagają pełnej spójności transakcyjnej, którą gwarantuje Postgres. Cassandra (model BASE) oferuje jedynie spójność ostateczną, co w medycynie jest niedopuszczalne ze względów prawnych i bezpieczeństwa pacjenta.

Elastyczność Chatbota: Zapytania w chatbocie medycznym często wymagają złożonych złączeń (JOIN) i filtrowania, w czym bazy relacyjne (Postgres) dominują nad ograniczonym modelem zapytań Cassandry.

Optymalizacja Kosztów: PostgreSQL jest "lżejszy" w konteneryzacji i testowaniu rozproszonych scenariuszy I/O na mniejszą skalę niż wymagająca sprzętowo Cassandra.


<!-- https://medium.com/@faizanhaidar48/why-chat-messaging-real-time-apps-prefer-cassandra-over-postgres-the-internal-story-explained-a4777b36f2fd -->

<!-- notion uzywa postgre https://haiderzdbre.substack.com/p/notion-postgresql-sharding-without-downtime -->