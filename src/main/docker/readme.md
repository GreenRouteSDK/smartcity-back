# Docker deployment guide (with configuration)

The docker deployment uses the following services:

- Back-end:                 smart-sdk-back:8080
- FrontEnd:                 smart-sdk-front:4200
- MongoDB:                  mongodb://smart-sdk-mongodb:27017
- Keyrock IdentityManager:  http://smart-sdk-keyrock:5000

Additional Services:
- OrionContextBroker:       http://smart-sdk-orion:1026
- QuantumLeap
- Grafana
- CrateDB

Using the docker-compose.yml file you can be able to test and deploy the application
using the previosuly made docker images from each component.

However, in order to adapt the application to your needs (the ideal way), it is
necessary to perform a few configurations on the smart-sdk-back and smart-sdk-front.


## Configure backend to deploy and generate docker image

1. Modify configuration in *application.yml*

**Mail server config, It uses gmail smtp server fo testing purposes:**

```
  spring.mail.host: smtp.gmail.com
  spring.mail.port: 587
  spring.mail.username: acccount@gmail.com
  spring.mail.password: XXXXXXX
```

**Front-end references**

```
front.url: localhost/#
front.image-url: localhost
```

2. Deploy and create .war at root of smartcity-back/

```
sudo mvn -P production clean package docker:build
```

3. Add tag to recently created image and push *(optional)*

```
docker login
docker tag smart-sdk-back samjm/smart-sdk-back:latest
docker push samjm/smart-sdk-back:1.0
```

## Configure and compile Front-end in smartcity-front/

1. Modify */src/environments/environment.prod.ts* to target backend URL along with

```
export const environment = {
  production: true,
  backend_sdk: 'http://localhost:8080/back-sdk',
  alerts_url: 'http://localhost:8443/#/',
  statistics_url: 'http://localhost:3000/dashboard/db/airquality-dashboard',
  routingmap_url: 'http://localhost:8081/'
};
```

2. Build angular application in smartcity-front/ root

```
ng build --env=prod
```

3. Build docker image. Dockerfile uses nginx-alpine and copy compiled /dist files.

```
sudo docker build -t smart-sdk-front .
```

4. Tag and push image *(optional)*

```
sudo docker tag smart-sdk-front samjm/smart-sdk-front:latest

sudo docker push samjm/smart-sdk-front:latest
```


## Mongo seeder. To load initial data to mongodb

1. Verify the *HOST* on *sample-data/import.sh* corresponds to the container name in docker-compose.yml file.

HOST=smart-sdk-mongodb
PORT=27017

2. Create docker image from smartcity-back/src/main/docker/mongo-import

```
docker build -t mongo-seeder .
docker tag mongo-seeder samjm/mongo-seeder:latest
docker push samjm/mongo-seeder:latest
```


Alternatively, you can use a compose file using a reverse-proxy and ssl for
a secure application, as seen in the docker-compose-proxy.yml file.

## Create docker image for reverseproxy found at smartcity-back/src/main/docker/reverseproxy

1. Change *server_name* in *nginx.conf* for domain name to use.

**TODO Further changes to include ssl**

2. Build image

  ```
  sudo docker build -t reverseproxy .
  ```

3. Tag and push to your repo *(optional)*

  ```
  sudo docker tag reverseproxy samjm/reverseproxy:1.0
  sudo docker push samjm/reverseproxy:1.0
  ```

Notice that the references you set here, needs to be updated in the previous urls

location path in nginx.conf (configured domain at hosts)

////////////////////////

#Docker guide of application ready-to-use:

#Testing step by step by using docker-machine VMs.

1. Create two VM

Pre-requisites:

Install virtualbox
sudo apt-get install virtualbox


docker-machine create --driver virtualbox myvm1
docker-machine create --driver virtualbox myvm2
docker-machine ls  #To see the virtual ip adress used in next step

2. Set main node for swarm

docker-machine ssh myvm1 "docker swarm init --advertise-addr 192.168.99.100"

Returns token to add workers to the swarm

 3. Add worker node to the swarm

docker-machine ssh myvm2 "docker swarm join --token SWMTKN-1-4kwbrascwqpye99rcs252okjgbj67eg5hpachf9dppkh6x5ff7-3dxfvrmtzd71yyezx2e0l1v7z 192.168.99.100:2377"
docker-machine ssh myvm1 "docker node ls"

4. Use environment from main docker node

docker-machine env myvm1
eval $(docker-machine env myvm1)

5. Deploy application using the docker-compose file

docker stack deploy -c docker-compose.yml greenroute
docker stack ps greenroute

6. Remove application

docker stack rm greenroute

7. Close docker main node environment

eval $(docker-machine env -u)


## Configure Grafana, CrateDB, QuantumLeap with data from Mexico City's AirQualityObserved.

Once the services are running, you can access Grafana on the url 0.0.0.0:3000
and CrateDB on 0.0.0.0:4200 and QuantumLeap in 0.0.0.0:8668



###QuantumLeap

Verify that QuantumLeap is working correclty by querying:

0.0.0.0:8668/v2/version

### Create a susbscription in OCB to AirQualityObserved where the notifications fall into QuantumLeap

curl -v localhost:1026/v2/subscriptions -s -S -H 'Content-Type: application/json' --header "Fiware-Service:airquality" --header "Fiware-ServicePath:/" -d @- <<EOF
 {
  "description": "Quantum Temp Name",
  "subject": {
    "entities": [
      {
        "idPattern": ".* ",
        "type": "AirQualityObserved"
      }
    ],
    "condition": {
      "attrs": [
        "CO",
        "O3",
        "PM10",
        "SO2",
        "NO2",
        "temperature",
        "relativeHumidity",
        "dateObserved"
      ]
    }
  },
  "notification": {
    "attrs": [
      "id",
      "CO",
      "O3",
      "PM10",
      "SO2",
      "NO2",
      "temperature",
      "relativeHumidity",
      "dateObserved",
      "address",
      "location",
      "latitude",
      "longitude"
    ],
    "attrsFormat": "normalized",
    "http": {
      "url": "http://0.0.0.0:8668/v2/notify"
    },
    "metadata": [
      "dateCreated",
      "dateModified"
    ]
  },
  "throttling": 1
}
EOF




##CrateDB

###1. Instalar Crate c/docker

We need to create two additional tables in order to provide a better interface in
the Grafana dashboard.


We can access cratedb via command-line or through web browser in the 0.0.0.0:4200

1. To access through console, connect to the container:

docker exec -ti cratedb /bin/sh

2. Call the bash console, called crash:
crash



###2. Crear tablas de estaciones y contaminantes e insertar datos.

CREATE TABLE IF NOT EXISTS "doc"."etstation" (
  "entity_id" STRING,
  "name" STRING,
  "acron" STRING,
  PRIMARY KEY ("entity_id")
)

CREATE TABLE IF NOT EXISTS "doc"."etpollutant" (
  "idpollutant" INTEGER,
  "name" STRING,
  "namefront" STRING,
  PRIMARY KEY ("idpollutant")
)


*TODO Fix import with json file*

insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150020109','Acolman','ACO');
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090120609','Ajusco Medio','AJM');
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090120400','Ajusco','AJU');
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090050301','Aragón','ARA');
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150130101','Atizapán','ATI');
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090020201','Azcapotzalco','AZC');
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090140201','Benito Juárez','BJU')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090020301','Camarones','CAM')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090030501','Centro de Ciencias de la Atmósfera','CCA')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090070111','Cerro de la Estrella','CES')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150250109','Chalco','CHO')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090030303','Coyoacán','COY')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090040109','Cuajimalpa','CUA')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150950109','Cuautitlán','CUT')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150570109','FES Acatlán','FAC')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090050809','Gustavo A. Madero','GAM')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090150409','Hospital General de México','HGM')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090050209','Inst. Mexicano del Petróleo','IMP')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150620109','Investigaciones Nucleares','INN')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150990113','Montecillo','MON')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090060101','Iztacalco','IZT')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484151040203','La Presa','LPR')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090170127','Merced','MER')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090160609','Miguel Hidalgo','MGH')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090090104','Milpa Alta','MPA')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150580115','Nezahualcóyotl','NEZ')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090100127','Pedregal','PED')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150330327','San Agustín','SAG')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090040309','Santa fe','SFE')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090050701','San Juan Aragón','SJA')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090030109','Santa Ursula','SUR')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090130309','Tlahuac','TAH')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090050404','Cerro del Tepeyac','TEC')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484151040115','Tlalnepantla','TLA')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484151090101','Tultitlán','TLI')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090120209','Tlalpan','TPN')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090030401','UAM Xochimilco','UAX')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150330415','Xalostoc','XAL')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090150101','Lagunilla','LAG')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150330201','Los Laureles','LLA')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090100209','Plateros','PLA')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090160309','Tacuba','TAC')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090030201','Taxqueña','TAX')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090070219','UAM Iztapalapa','UIZ')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484150200109','Villa de las flores','VIF')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090050501','Vallejo','VAL')
insert into etstation (entity_id, name, acron) values('CDMX-AmbientObserved-484090050101','La Villa','LVI')

insert into etpollutant (idpollutant, name, namefront) values(1,'temperature','Temperature');
insert into etpollutant (idpollutant, name, namefront) values(2,'relativehumidity','Relative Humidity');
insert into etpollutant (idpollutant, name, namefront) values(3,'co','Carbon Monoxide (CO)');
insert into etpollutant (idpollutant, name, namefront) values(4,'no2','Nitrogen Dioxide (NO2)');
insert into etpollutant (idpollutant, name, namefront) values(5,'o3','Ozone (O3)');
insert into etpollutant (idpollutant, name, namefront) values(6,'so2','Sulfur Dioxide (SO2)');
insert into etpollutant (idpollutant, name, namefront) values(7,'pm10','Particulate Matter (PM10)');




### Grafana


Grafana comes with an API which lets you manage many options including dashboards
and datasources.

1. As a first step, you need to obtain a key to use the API:

POST http://admin:admin@0.0.0.0:3000/api/auth/keys
Content-Type: application/json

{"name":"apikeycurl", "role": "Admin"}


It returns a key, which you'll need to send as an "Authorization" header in the
following steps.

{
	"name": "apikeycurl",
	"key": "eyJrIjoiUDNGQlM5YldXbUdVU2JreDJiVkZDYW81aWZCTlZFSlkiLCJuIjoiYXBpa2V5Y3VybCIsImlkIjoxfQ=="
}

2. Create a CrateDB datasource where you need to incidate the cratedb url within the json payload

POST http://0.0.0.0.3000/api/datasources
Content-Type: "application/json"
Authorization: "Bearer eyJrIjoiUDNGQlM5YldXbUdVU2JreDJiVkZDYW81aWZCTlZFSlkiLCJuIjoiYXBpa2V5Y3VybCIsImlkIjoxfQ=="

{
  "id": null,
  "orgId": 1,
  "name": "AIRQUALITY",
  "type": "crate-datasource",
  "typeLogoUrl": "public/plugins/crate-datasource/img/crate_logo.png",
  "access": "proxy",
  "url": "http://0.0.0.0:4200",
  "password": "",
  "user": "",
  "database": "",
  "basicAuth": false,
  "isDefault": true,
  "jsonData": {
    "keepCookies": [],
    "schema": "doc",
    "table": "etairqualityobserved",
    "timeColumn": "dateobserved",
    "timeInterval": "auto_gf"
  },
  "readOnly": false
}


3. Import dashboard rawdash.json


curl -X POST  -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer eyJrIjoiUDNGQlM5YldXbUdVU2JreDJiVkZDYW81aWZCTlZFSlkiLCJuIjoiYXBpa2V5Y3VybCIsImlkIjoxfQ==" -d @rawdash.json http://0.0.0.0:3000/api/dashboards/db
