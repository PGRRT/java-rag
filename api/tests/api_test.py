from fastapi.testclient import TestClient
from api.entry import create_api

test_client = TestClient(create_api())

# TODO :: async tests?
# TODO :: Add testing for nonexistent conversation_id

def test_valid_query() -> None:
    response = test_client.post("/query/10",json={"message":"This is a message"})
    assert response.status_code == 200

    json = response.json()

    assert "message" in json
    assert "contexts" in json
    assert type(json["contexts"]) is list
    assert len(json["contexts"]) > 0


def test_invalid_query() -> None:
    pass # TODO


def test_valid_upload_file() -> None:

    files = [
        ("files", ("file1",b"Example file 1 content","text/plain")),
        ("files",("file2",b"Example file 2 content","text/plain"))
    ]

    response = test_client.post("/upload/10",files=files)

    json = response.json()
    assert response.status_code == 200
    assert "success" in json
    assert json["success"] == True
    assert "message" in json

def test_upload_no_files() -> None:

    response = test_client.post("/upload/10")

    json = response.json()

    print(json)
    assert response.status_code == 400
    assert "success" in json
    assert json["success"] == False
    assert "message" in json
