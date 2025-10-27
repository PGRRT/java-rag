# Retrieval-Augmented Generation (RAG) Research Project

### Contributing
#### Pre-commit
Pre commit is used to automatically run linters, formatters and ensures that all tests are passing

> [!NOTE]
> Make sure you have installed all the important packages in api/requirements.txt

1.Install **pre-commit** command with  ``` pip install pre-commit ```.\
2. run ```pre-commit install``` to install git hooks.\
3. install **pytest** with ```pip install pytest``` for python test. If it is not installed, python tests will not run.\
4. The hooks will run automatically after each commit. Or you can run them manually with ```pre-commit run --all-files```.

# How to run

### Running RAG API


> [!NOTE]
> Project is developed for Python 3.11.9

> [!NOTE]
> As of the current state port has to be set to 8081 for it to work with frontend

1. From repository root change directory to api's directory ```cd ./api```
2. Create virtual environment (optional) ```python -m venv .venv && source ./.venv/bin/activate```
3. install requirements ```pip install -r requirements.txt```
4. Run the development API ```fastapi dev --entrypoint api.entry:create_api --port 8081```
(On windows small change in second step, use: .venv\Scripts\activate.bat)


### Running frontend Web application (django)

1. From repository root change directory to frontend's directory ```cd ./frontend```
2. Create virtual environment (optional) ```python -m venv .venv && source ./.venv/bin/activate```
3. install requirements ```pip install -r requirements.txt```
4. Apply database migrations ```python manage.py makemigrations && python manage.py migrate```
4. Run the development app ```python manage.py runserver```

