import nmpi
from pprint import pprint

client = nmpi.Client(username="Lxnus")

token = client.token

newClient = nmpi.Client(username="Lxnus", token=token)

job_id = newClient.submit_job(
    source="kernel.py",
    platform=nmpi.SPINNAKER,
    collab_id=79910,
    wait=True)

newClient.job_status(job_id=job_id)

job = newClient.get_job(job_id=job_id)

pprint(job)

filenames = newClient.download_data(job=job, local_dir=".")

image_filenames = [name for name in filenames if name.endswith(".png")]
print(image_filenames)

for image_filename in image_filenames:
    print (image_filename)
