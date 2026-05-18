# build-dev:
# 	docker compose -f compose.dev.yaml build

# start-dev:
# 	docker compose -f compose.dev.yaml up

# restart-dev:
# 	docker compose -f compose.dev.yaml down
# 	docker compose -f compose.dev.yaml up

# # add eureka service if needed
# restart-backend-dev:
# 	docker compose -f compose.dev.yaml restart user-service chat-service gateway

# stop-dev:
# 	docker compose -f compose.dev.yaml down

# # add eureka service if needed
# rebuild-backend-dev:
# 	docker compose -f compose.dev.yaml stop user-service chat-service gateway
# 	docker compose -f compose.dev.yaml build user-service chat-service gateway

# rebuild-user-dev:
# 	docker compose -f compose.dev.yaml stop user-service
# 	docker compose -f compose.dev.yaml build user-service 

# rebuild-api-dev:
# 	docker compose -f compose.dev.yaml stop api
# 	docker compose -f compose.dev.yaml build api

# rebuild-frontend-dev:
# 	docker compose -f compose.dev.yaml stop frontend
# 	docker compose -f compose.dev.yaml build frontend

# rebuild-dev:
# 	docker compose -f compose.dev.yaml down
# 	docker compose -f compose.dev.yaml build
# 	docker compose -f compose.dev.yaml up

# rebuild-dev-full:
# 	docker compose -f compose.dev.yaml down
# 	docker compose -f compose.dev.yaml build --no-cache
# 	docker compose -f compose.dev.yaml up

# build-prod:
# 	docker compose -f compose.dev.yaml build

# start-prod:
# 	docker compose -f compose.prod.yaml up

# stop-prod:
# 	docker compose -f compose.prod.yaml down


# rebuild-prod:
# 	docker compose -f compose.prod.yaml down
# 	docker compose -f compose.prod.yaml build --no-cache
# 	docker compose -f compose.prod.yaml up

# clean-all:
# 	docker compose -f compose.dev.yaml down
# 	docker compose -f compose.prod.yaml down
# 	docker system prune -f
# 	docker builder prune -a -f

# # Logs commands
# logs-api-dev:
# 	docker compose -f compose.dev.yaml logs -f api

# logs-frontend-dev:
# 	docker compose -f compose.dev.yaml logs -f frontend


# logs-backend-dev:
# 	docker compose -f compose.dev.yaml logs -f eureka user-service chat-service gateway

# logs-dev-all:
# 	docker compose -f compose.dev.yaml logs -f


# --- KONFIGURACJA ---
# Flagi uwzględniające pliki .env dla poprawnego wstrzykiwania zmiennych (np. hasła Redisa)
DEV = docker compose -f compose.dev.yaml --env-file .env --env-file .env.dev.local
PROD = docker compose -f compose.prod.yaml --env-file .env.prod.local

# --- DEVELOPMENT ---

build-dev:
	$(DEV) build

start-dev:
	$(DEV) up

stop-dev:
	$(DEV) down

restart-dev:
	$(DEV) down
	$(DEV) up

restart-backend-dev:
	$(DEV) restart user-service chat-service ai-service gateway hybrid-rag mcp-server medical-agent

rebuild-user-dev:
	$(DEV) stop user-service
	$(DEV) build user-service
	$(DEV) up user-service

rebuild-medical-agent-dev:
	$(DEV) stop medical-agent
	$(DEV) build medical-agent
	$(DEV) up medical-agent

rebuild-hybrid-rag-dev:
	$(DEV) stop hybrid-rag mcp-server medical-agent
	$(DEV) build hybrid-rag mcp-server medical-agent
	$(DEV) up hybrid-rag mcp-server medical-agent

rebuild-frontend-dev:
	$(DEV) stop frontend
	$(DEV) build frontend
	$(DEV) up frontend

rebuild-backend-dev:
	$(DEV) stop user-service chat-service ai-service gateway hybrid-rag mcp-server medical-agent
	$(DEV) build user-service chat-service ai-service gateway hybrid-rag mcp-server medical-agent
	$(DEV) up user-service chat-service ai-service gateway hybrid-rag mcp-server medical-agent

rebuild-dev:
	$(DEV) down
	$(DEV) build
	$(DEV) up

rebuild-dev-full:
	$(DEV) down
	$(DEV) build --no-cache
	$(DEV) up


build-prod:
	$(PROD) build

start-prod:
	$(PROD) up

stop-prod:
	$(PROD) down

rebuild-prod:
	$(PROD) down
	$(PROD) build --no-cache
	$(PROD) up


config-dev:
	$(DEV) config

logs-medical-agent-dev:
	$(DEV) logs -f medical-agent

logs-hybrid-rag-dev:
	$(DEV) logs -f hybrid-rag mcp-server medical-agent

logs-frontend-dev:
	$(DEV) logs -f frontend

logs-backend-dev:
	$(DEV) logs -f eureka user-service chat-service ai-service gateway hybrid-rag mcp-server medical-agent

logs-dev-all:
	$(DEV) logs -f


clean-all:
	$(DEV) down
	$(PROD) down
	docker system prune -f
	docker builder prune -a -f