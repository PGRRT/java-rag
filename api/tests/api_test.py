from fastapi import UploadFile
from fastapi.testclient import TestClient
import pymupdf
from api.entry import create_api,ApiMode
import io

test_client = TestClient(create_api(mode = ApiMode.Testing))

# TODO :: async tests?
# TODO :: Add testing for nonexistent conversation_id

def test_valid_query() -> None:
    """ User properly queries existing conversation and receives valid response. """
    response = test_client.post("/query/10",json={"query":"This is a message"})
    json = response.json()

    assert response.status_code == 200
    assert "message" in json
    assert "contexts" in json
    assert type(json["contexts"]) is list


def test_invalid_query() -> None:
    """ Querying a convrsation that doesn't exist should return an error. """
    pass # TODO


def test_valid_upload_file() -> None:

    doc = pymupdf.open()
    page = doc.new_page()
    page.insert_text((72,72),"Example text")
    pdf_bytes = doc.write()
    doc.close()

    print(pdf_bytes)

    fake_pdf = io.BytesIO(pdf_bytes)
    fake_pdf.name = "test.pdf"

    response = test_client.post(
        "/upload/169",
        files=[
            ("files",(fake_pdf.name,fake_pdf,"application/pdf"))
        ]
    )

    json = response.json()

    assert response.status_code == 201
    assert "success" in json
    assert json["success"] == True
    assert "message" in json

def test_upload_invalid_file() -> None:
    """ Uploading invalid file formats e.g. file.exe"""
    pass # TODO

def test_upload_no_files() -> None:
    """No files passed to upload endpoint should return an error. """

    response = test_client.post("/upload/10")
    json = response.json()
    print(json)

    assert response.status_code == 400
    assert "success" in json
    assert json["success"] == False
    assert "message" in json


def test_delete_conversation() -> None:
    """ Deleting existing conversation. """
    response = test_client.delete("delete/1")
    json = response.json()

    assert response.status_code == 200
    assert "status" in json
    assert json["status"] == True


def test_delete_invalid_conversation() -> None:
    """ Deleting conversation that doesn't exist should return an error. """
    pass # TODO


