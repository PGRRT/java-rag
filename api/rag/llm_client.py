from abc import ABC, abstractmethod
from typing_extensions import override
from openai import OpenAI
from dotenv import load_dotenv
import httpx
import os


class LLM(ABC):
    @abstractmethod
    def generate_response(self, prompt: str) -> str:
        pass

    system_prompt = ("""Odpowiadaj na pytania użytkownika dotyczące fragmentow źródeł danych wymienionych w wiadomości.
Przykład:

Pytanie użytkownika: “Potrzebuję danych dotyczących migracji ludności w UE po 2015 roku.”
Źródła wymienione przez użytkownika: Eurostat, ONZ, krajowe biura statystyczne""")

    system_prompt_op = """Odpowiadaj na pytania użytkownika dotyczące potencjalnych źródeł danych wymienionych w wiadomości.

Jeśli wiadomość NIE zawiera żadnych informacji na temat źródeł danych (lub nie wynika z niej, o jakie źródła chodzi), odpowiedz wyłącznie:
**Masz za mało kontekstu, by udzielić rekomendacji.**
Nie przeprowadzaj żadnego rozumowania ani nie podawaj przykładowych źródeł.

W przeciwnym razie stosuj się do poniższych wytycznych:

- Najpierw przeanalizuj pytanie, aby określić kluczowe potrzeby informacyjne i kontekst zadania.
- Następnie wygeneruj rozumowanie:
    - Przeprowadź analizę, jakie typy lub rodzaje źródeł danych mogą być odpowiednie dla danego pytania na podstawie informacji zawartych pod pytaniem.
    - Oceń, jakie są potencjalne silne i słabe strony tych źródeł (wiarygodność, dostępność, aktualność, zakres).
    - Wyszczególnij, na jakie kryteria należy zwrócić uwagę przy wyborze źródła danych.
- Dopiero po przeprowadzeniu powyższego rozumowania, dokonaj końcowego wyboru, rekomendacji lub klasyfikacji potencjalnych źródeł danych.
- W przypadku gdy pytanie wskazuje, że użytkownik oczekuje konkretnych przykładów, podaj 2–3 przykłady źródeł danych, stosując jasne opisy (np. '[tytuł bazy danych]', '[nazwa repozytorium]', '[rodzaj statystyki publicznej]').
- Jeśli zadanie jest złożone i wymaga kilku kroków analizy, realizuj każdy etap po kolei do momentu pełnego rozwiązania zadania.

Format odpowiedzi:
- Każda odpowiedź powinna mieć wyraźnie oznaczone sekcje:
    - “Rozumowanie”: Szczegółowe omówienie procesu wyboru źródeł danych, analiza kryteriów, identyfikacja potencjalnych rozwiązań.
    - “Rekomendowane źródła danych” lub “Wynik końcowy”: Konkretne propozycje źródeł danych i krótkie uzasadnienie wyboru.
- Format odpowiedzi: Przejrzysty tekst podzielony na sekcje. Długość odpowiedzi uzależnij od złożoności pytania (zazwyczaj 1–3 akapity w części rozumowania, lista 2–5 pozycji w rekomendacjach).

Przykład:

Pytanie użytkownika: “Potrzebuję danych dotyczących migracji ludności w UE po 2015 roku.”
Źródła wymienione przez użytkownika: Eurostat, ONZ, krajowe biura statystyczne

Rozumowanie: W tym przypadku potrzebne są wiarygodne, aktualne i porównywalne dane statystyczne dotyczące migracji wewnątrz UE po 2015 roku. Najlepszym źródłem będą oficjalne instytucje statystyczne i międzynarodowe bazy danych, które regularnie monitorują ruchy ludności.
Rekomendowane źródła danych:
- Eurostat (statystyki migracji w UE)
- [Nazwa bazy danych ONZ]
- National Statistics Offices wybranych krajów UE

WAŻNE: Najpierw zawsze proces rozumowania, a dopiero potem gotowe rekomendacje lub wyniki!

---

Przypomnienie: Jeśli nie masz informacji o źródłach danych w zadaniu, odpowiedz tylko: “Masz za mało kontekstu, by udzielić rekomendacji.”
W pozostałych przypadkach: najpierw rozumowanie i analiza kryteriów, dopiero potem rekomendacje potencjalnych źródeł danych lub wyniki końcowe. Odpowiedzi dziel jasno na sekcje “Rozumowanie” oraz “Rekomendowane źródła danych / Wynik końcowy”.

# Output Format

Odpowiedź w języku polskim, przejrzysty tekst bez kodu, zawsze z wyraźnym podziałem na wymagane sekcje.
Jeśli brak informacji o źródłach: wyłącznie jedno krótkie zdanie bez dodatkowych akapitów.
W innych wypadkach: ustrukturyzowana, podzielona odpowiedź (z sekcjami).

# Przypomnienie

W pierwszej kolejności sprawdź, czy wiadomość zawiera informacje o źródłach danych. Jeśli nie, odpowiedz: “Masz za mało kontekstu, by udzielić rekomendacji.”
W przeciwnym razie: rozumowanie → rekomendacje źródeł, zgodnie z instrukcją."""


class OpenAILLM(LLM):
    def __init__(self) -> None:
        load_dotenv()
        self.client: OpenAI = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

    @override
    def generate_response(self, prompt: str) -> str:
        response = self.client.responses.create(
            model="gpt-4.1-mini",
            temperature=0.0,
            input=[
                {"role": "system", "content": self.system_prompt},
                {"role": "user", "content": prompt},
            ],
        )

        # TODO :: Error handling
        try:
            return response.output[0].content[0].text  # type: ignore
        except Exception:
            return "Generation failed"


class BielikLLM(LLM):
    def __init__(
        self,
        api_url: str,
        username: str,
        password: str,
        max_response_length: int = 4096,
        temperature: float = 0.0,
    ) -> None:
        self.client = httpx.Client(auth=(username, password), verify=False)
        """HTTP client"""

        self.api_url: str = api_url
        """Bielik API URL"""

        self.max_response_length: int = max_response_length
        """Number of characters to ouput for the llm"""

        self.temperature: float = temperature
        """LLM Temperature"""

    @override
    def generate_response(self, prompt: str) -> str:
        response = self.client.put(
            url=self.api_url,
            headers={"Accept": "application/json", "Content-Type": "application/json"},
            json={
                "messages": [
                    {"role": "system", "content": self.system_prompt},
                    {"role": "user", "content": prompt},
                ],
                "temperature": self.temperature,
                "max_length": self.max_response_length,
            },
            timeout=60 * 60,
        )

        response = response.json()
        print(response)

        return response["response"]
        # return str(response)


if __name__ == "__main__":
    # openai = OpenAILLM()
    # prompt = "What is your name?"
    # openai.generate_response(prompt)

    load_dotenv()

    bielik = BielikLLM(
        api_url=os.getenv("PG_API_URL") or "",
        username=os.getenv("PG_API_USERNAME") or "",
        password=os.getenv("PG_API_PASSWORD") or "",
    )

    prompt = "What is your name?"
    bielik.generate_response(prompt)
