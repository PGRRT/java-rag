from django.http import HttpResponse, HttpRequest
from django.shortcuts import render


# Create your views here.


def index(request: HttpRequest) -> HttpResponse:
    context = {"chats": ["Chat 1", "Chat 2", "Chat 3"]}
    return HttpResponse(
        render(request=request, template_name="ragwebapp/index.html", context=context)
    )
