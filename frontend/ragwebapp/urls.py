from django.urls import path

from . import views

app_name = "ragwebapp"

urlpatterns = [path("", views.index, name="index")]
