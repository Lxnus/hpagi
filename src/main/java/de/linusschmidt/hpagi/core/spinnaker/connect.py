import nmpi
from pprint import pprint

client = nmpi.Client(username="Lxnus")

token = client.token

newClient = nmpi.Client(username="Lxnus", token=token)

job = newClient.submit_job(
    source="kernel.py",
    platform=nmpi.SPINNAKER,
    collab_id=79910,
    wait=True)

filenames = newClient.download_data(job=job, local_dir="")

image_filenames = [name for name in filenames if name.endswith(".png")]
print(image_filenames)

for image_filename in image_filenames:
    print(image_filename)
