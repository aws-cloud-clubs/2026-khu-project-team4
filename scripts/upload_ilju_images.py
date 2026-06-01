import boto3
import json
import os
from dotenv import load_dotenv
from pathlib import Path

BASE_DIR = Path(__file__).parent.parent
load_dotenv(BASE_DIR / ".env")

AWS_ACCESS_KEY = os.getenv("AWS_ACCESS_KEY")
AWS_SECRET_KEY = os.getenv("AWS_SECRET_KEY")
AWS_REGION = os.getenv("AWS_REGION")
AWS_BUCKET = os.getenv("AWS_BUCKET")

IMAGE_DIR = BASE_DIR / "src/main/resources/data/ilju-animals-images"
JSON_PATH = BASE_DIR / "src/main/resources/data/ilju-animals.json"

s3 = boto3.client(
    "s3",
    aws_access_key_id=AWS_ACCESS_KEY,
    aws_secret_access_key=AWS_SECRET_KEY,
    region_name=AWS_REGION,
)

with open(JSON_PATH, "r", encoding="utf-8") as f:
    animals = json.load(f)

for animal in animals:
    name = animal["name"]
    image_path = IMAGE_DIR / f"{name}.png"

    if not image_path.exists():
        print(f"[SKIP] 이미지 없음: {name}.png")
        continue

    key = f"ilju/{name}.png"
    s3.upload_file(
        str(image_path),
        AWS_BUCKET,
        key,
        ExtraArgs={"ContentType": "image/png"},
    )

    url = f"https://{AWS_BUCKET}.s3.{AWS_REGION}.amazonaws.com/{key}"
    animal["imageUrl"] = url
    print(f"[OK] {name} → {url}")

with open(JSON_PATH, "w", encoding="utf-8") as f:
    json.dump(animals, f, ensure_ascii=False, indent=2)

print("\n완료: ilju-animals.json 업데이트됨")
