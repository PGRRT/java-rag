#!/bin/bash
echo "Rozpoczynam test Load Balancingu z pauzami..."
for i in {1..25}
do
   curl -s "http://localhost:8080/api/v1/chats?page=0&size=10" > /dev/null
   echo "Zapytanie $i wysłane"
   sleep 1  # Dodajemy sekundę przerwy
done
