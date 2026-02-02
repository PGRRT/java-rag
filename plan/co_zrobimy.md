## 1 
ARCHITEKTURA SYSTEMU (HUB AND SPOKE MODEL)

System został zaprojektowany w modelu scentralizowanego repozytorium (Hub w Europie) łączącego się z regionalnymi węzłami danych (Spokes w USA/Azji).

Kluczowe Komponenty:

Edge Gateway (Traefik): Odpowiada za SSL Termination, wstępną walidację i routing na podstawie GeoIP dla użytkowników niezalogowanych.

Middleware (Java 21 + Spring Boot): Serce systemu wykorzystujące Watki Wirtualne (Project Loom) do orkiestracji zapytań wzorcem Scatter-Gather.

Regionalne Centra Danych:

W każdym regionie (EU, USA, ASIA) znajdują się 2-3 instancje PostgreSQL pracujące w klastrze (High Availability).

Dane medyczne podlegają rygorowi Data Residency - fizycznie pozostają w regionie pochodzenia.

STRATEGIA IDENTYFIKACJI I ZAPISU (IDENTITY AND LOCATION)
System musi precyzyjnie określić, gdzie zapisać i skąd czytać dane:

## 2
Scenariusz 
Użytkownik Zalogowany

Mechanizm Identyfikacji
JWT Claims

Miejsce Zapisu / Home Region
Region zapisany w tokenie (np. USA)

<!-- /// -->

Scenariusz 
Użytkownik Niezalogowany

Mechanizm Identyfikacji
GeoIP Lookup

Miejsce Zapisu / Home Region
Region najbliższy fizycznie (przez IP)

## 3
<!-- NIE IMPLEMENTUJEMY -->
PRZEPROWADZKA UZYTKOWNIKA (DATA MIGRATION): W przypadku zmiany stałego miejsca zamieszkania, system nie migruje danych automatycznie ze względu na spójność prawną dokumentacji medycznej.

Użytkownik posiada funkcjonalność Move Data Button.

Uruchamia ona asynchroniczny proces transferu rekordów między bazami regionalnymi przy zachowaniu integralności UUID.

## 4
SCENARIUSZ BADAWCZY: GLOBALNY CHAT PUBLICZNY

Zapytanie o historię czatu: Użytkownik chce widzieć globalną konwersację.

Scatter (Rozproszenie): Java (Loom) wysyła równoległe zapytania do wszystkich baz regionalnych (EU, USA, ASIA,...).

Gather (Agregacja): System czeka na odpowiedzi. Watki wirtualne pozwalają na tanie parkowanie operacji podczas oczekiwania na odpowiedź zza oceanu (Latency).

Baseline (FDW): Jako punkt odniesienia służy PostgreSQL Foreign Data Wrapper, gdzie baza w EU sama próbuje scalić dane z USA.

