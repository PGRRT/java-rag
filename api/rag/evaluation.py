# import pandas as pd
# import json
# import os
# from dotenv import load_dotenv
#
# from src.rag import ClassicRAG
# from src.llm_client import BielikLLM, OpenAILLM
#
#
# load_dotenv()
#
# bielik = BielikLLM(
#     api_url=os.getenv("PG_API_URL") or "",
#     username=os.getenv("PG_API_USERNAME") or "",
#     password=os.getenv("PG_API_PASSWORD") or "",
# )
#
# chatgpt = OpenAILLM()
# rag = ClassicRAG(bielik)
#
#
# # Wczytanie datasetu
# dataset = pd.read_json("../dataset/medical-exams-LEK-PL-210-questions.json")
#
# output_file = "../dataset/rag_output.json"
#
# # Jeśli plik istnieje, wczytujemy istniejące wyniki
# if os.path.exists(output_file):
#     with open(output_file, "r", encoding="utf-8") as f:
#         output = json.load(f)
# else:
#     output = {"results": []}
#
# index = 0
# for idx, row in dataset.iterrows():
#     query_id = index
#     index += 1
#     query_text = row['question_w_options']
#     gt_answer = row['answer']
#
#     # Uruchomienie RAG dla pytania
#     response_obj = rag.process_query_evaluate(query_text, 0)
#
#     # Przygotowanie rekordu
#     result = {
#         "query_id": query_id,
#         "query": query_text,
#         "gt_answer": gt_answer,
#         "response": str(response_obj.get("response", "")),
#         "retrieved_context": []
#     }
#
#     # Dodanie kontekstu
#     if "contexts" in response_obj:
#         for i, context in enumerate(response_obj["contexts"]):
#             context_entry = {
#                 "doc_id": i,
#                 "text": context
#             }
#             result["retrieved_context"].append(context_entry)
#
#     # Dodanie nowego rekordu do wyników
#     output["results"].append(result)
#
#     # Zapis po każdym pytaniu
#     with open(output_file, "w", encoding="utf-8") as f:
#         json.dump(output, f, ensure_ascii=False, indent=2)
#
#     print(f"Przetworzono pytanie {query_id}")
#
# print("Zakończono przetwarzanie wszystkich pytań.")
#
#
#
#





#
# from ragchecker import RAGResults, RAGChecker
# from ragchecker.metrics import all_metrics
# import json
#
# with open("../dataset/rag_output.json") as fp:
#     rag_results = RAGResults.from_json(fp.read())
#
# evaluator = RAGChecker(
#     extractor_name="openai/gpt-5.1",
#     checker_name="openai/gpt-5.1",
#     batch_size_extractor=32,
#     batch_size_checker=32
# )
#
# evaluator.evaluate(rag_results, all_metrics)
# print(rag_results)
#
# output_file = "../dataset/rag_evaluation_results.json"
# with open(output_file, "w", encoding="utf-8") as f:
#     json.dump(rag_results.to_dict(), f, ensure_ascii=False, indent=4)


import json
import re


def compute_accuracy(data):
    """
    Liczy accuracy dla ChatGPT na podstawie formatu:
    {
        "results": [
            {
                "query_id": ...,
                "query": "...",
                "gt_answer": "B",
                "response": "Odpowiedź ... B. ...",
                ...
            },
            ...
        ]
    }
    """
    results = data["results"]
    correct = 0
    total = len(results)

    for item in results:
        gt = item["gt_answer"].strip().upper()

        # Wyciągamy pierwszą literę odpowiedzi z modelu (np. "B" z "B. komisji lekarskiej...")
        match = re.search(r'\b([A-E])\b', item["response"].upper())
        if match:
            pred = match.group(1)
        else:
            # Jeśli nie uda się znaleźć litery w odpowiedzi, traktujemy jako błędną
            pred = None

        if pred == gt:
            correct += 1

    accuracy = correct / total * 100 if total > 0 else 0
    return accuracy

# Przykład użycia:
with open("../dataset/rag_output.json") as f:
    data = json.load(f)
acc = compute_accuracy(data)
print(f"Accuracy ChatGPT: {acc:.2f}%")
