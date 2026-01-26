# Opóźnienia sieciowe z Polski (Warszawa)

Zestawienie średnich czasów odpowiedzi (**Round-Trip Time**) z Warszawy do kluczowych punktów na innych kontynentach. Dane odzwierciedlają realne pomiary infrastruktury z początku 2026 roku.

| Kontynent | Przykładowa lokalizacja | Średni ping (RTT) | Uwagi |
| :--- | :--- | :--- | :--- |
| **Europa** (wewnątrz) | Kraje UE (np. Berlin) | **22 ms** | Bezpośrednia trasa lądowa (najniższe opóźnienia) |
| **Ameryka Północna** | USA (Nowy Jork) | **98 ms** | Przez Londyn (główny skok transatlantycki) |
| **Azja** | Indie (Mumbai) | **148 ms** | Wydajne połączenie przez huby w UE i Bliski Wschód |
| **Afryka** | RPA (Kapsztad) | **170 ms** | Trasa południowa wzdłuż wybrzeży Afryki |
| **Australia i Oceania** | Australia (Sydney) | **298 ms** | Najdalsza fizyczna trasa (często przez Singapur lub USA) |
| **Ameryka Południowa** | Brazylia (São Paulo) | **224 ms** | Ruch przez USA (Frankfurt -> Londyn -> NY -> Miami) |
| **Ameryka Środkowa** | Panama | **187 ms** | Połączenie realizowane przez węzły w USA |

---

### Kluczowe wnioski z analizy (Polska 2026):

* **Standard USA:** Polska osiąga wynik poniżej **100 ms** do Nowego Jorku, co stawia nas w czołówce krajów o najlepszym dostępie do zasobów transatlantyckich.
* **Bariera Fizyczna:** Żaden kontynent (poza Europą) nie schodzi poniżej **90 ms** ze względu na prędkość światła w światłowodzie i odległości między kontynentami.
* **Węzeł Warszawski:** Dzięki lokalnym punktom wymiany ruchu (jak EPIX czy Equinix), czas odpowiedzi do Niemiec (18-22 ms) jest bazą dla wszystkich połączeń globalnych.

> **Źródło danych:** > - Real-time statistics: [WonderNetwork Warsaw](https://wondernetwork.com/pings/Warsaw)
<!-- https://wondernetwork.com/pings/Warsaw -->

<!-- jak biegna kable https://www.submarinecablemap.com/ -->
