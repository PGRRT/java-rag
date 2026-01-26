# Opóźnienia sieciowe z Europy (United Kingdom)

Zestawienie średnich maksymalnych czasów odpowiedzi (**Round-Trip Time**) z Europy do kluczowych punktów na innych kontynentach.

| Kontynent | Przykładowa lokalizacja | Maks. opóźnienie (RTT) |
| **Europa** (wewnątrz) | Kraje UE | **30 ms** |
| **Ameryka Północna** | USA (Nowy Jork – Londyn) | **90 ms** |
| **Azja** | Indie | **250 ms** |
| **Afryka** | RPA (Republika Południowej Afryki) | **350 ms** |
| **Australia i Oceania** | Australia | **405 ms** |
| **Ameryka Południowa** | Brazylia | **~220 ms** | Suma: Europa-USA (90ms) + USA-Brazylia (130ms) |
| **Ameryka Środkowa** | Panama | **~150 ms** | Suma: Europa-USA (90ms) + USA-Panama (60ms) |
---

### Krótki komentarz:
* **Ameryka Północna:** Połączenie Londyn–Nowy Jork to najszybsza trasa transatlantycka.
* **Azja:** Wybrane Indie są najszybszym punktem styku z Azją w tym zestawieniu (250 ms), podczas gdy np. Korea Południowa to już 470 ms.
* **Australia:** Ze względu na ogromny dystans fizyczny, opóźnienie przekracza 0,4 sekundy, co jest odczuwalne w aplikacjach czasu rzeczywistego.
* **Ameryka Południowa:** W Twoich danych brak bezpośredniego SLA z Europy do Ameryki Łacińskiej (zazwyczaj ruch idzie przez USA).

> Dane pochodzą z oficjalnych statystyk Verizon Enterprise (2026).
<!-- https://www.verizon.com/business/terms/global_latency_sla/?msockid=2d6a14cc42dd629935ef004243fc636a -->


<!-- jak biegna kable https://www.submarinecablemap.com/ -->