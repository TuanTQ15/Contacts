# Contacts
Manage your contact using API

API DOCUMENTATIONS
(github: https://github.com/nguyenkt99/contact-management)


BASE_URL: https://contact-ptit.herokuapp.com/api

REGISTER ACCOUNT
POST /register
Content-Type: application/json
{
    "fullName": "NGUYÊN",
    "email": "nguyen@gmail.com",
    "password": "123456"
}

Response successful
{
    "id": 5,
    "fullName": "NGUYÊN",
    "email": "nguyen@gmail.com",
    "password": "123456"
}

Response failed
{
    "status": "FORBIDDEN",
    "message": "Email đã tồn tại"
}


LOGIN
POST /login
Content-Type: application/json
{
    "email": "nguyen@gmail.com",
    "password": "123456"
}

Response successful
{
    "id": 1,
    "fullName": "NGUYÊN KT",
    "email": "nguyen@gmail.com"
}	

Response failed
{
    "status": "UNAUTHORIZED",
    "message": "Sai tài khoản mật khẩu"
}



ADD CONTACT
POST contacts/{accountId}	
Content-Type: application/json
{
    "name": "Han Sara",
    "address": "Đắk Lắk",
    "photo": "data:image/png;base64,iVBORw0KGgo...",
    "phones": [
        {
            "phoneNumber": "999",
            "type": "personal"
        },
        {
            "phoneNumber": "888",
            "type": "company"
        },
        {
            "phoneNumber": "777",
            "type": "education"
        }
    ],
    "emails": [
        {
            "emailAddress": "hansara@gmail.com",
            "type": "personal"
        },
        {
            "emailAddress": "hansara@vtn.vn",
            "type": "company"
        }
    ]
}

Response successful
{
    "id": 4,
    "name": "Han Sara",
    "address": "Đắk Lắk",
    "photo": "http://res.cloudinary.com/dksxh0tqy/image/upload/v1622135859/p8r9fx0xmzcobpuka7kz.png",
    "phones": [
        {
            "phoneNumber": "999",
            "type": "personal"
        },
        {
            "phoneNumber": "888",
            "type": "company"
        },
        {
            "phoneNumber": "777",
            "type": "education"
        }
    ],
    "emails": [
        {
            "emailAddress": "hansara@gmail.com",
            "type": "personal"
        },
        {
            "emailAddress": "hansara@vtn.vn",
            "type": "company"
        }
    ]
}



EDIT CONTACT
PUT /contacts/{accountId}/{id}
Content-Type: application/json
{
    "name": "Han Sara",
    "address": "Hàn Quốc",
    "photo": "",
    "phones": [
        {
            "phoneNumber": "99999999",
            "type": "company"
        }
    ],
    "emails": [
        {
            "emailAddress": "hansara@gmail.com",
            "type": "personal"
        },
        {
            "emailAddress": "han@vtv.vn",
            "type": "company"
        }
    ]
}

response successful
{
    "id": 4,
    "name": "Han Sara",
    "address": "Hàn Quốc",
    "photo": "http://res.cloudinary.com/dksxh0tqy/image/upload/v1622135859/p8r9fx0xmzcobpuka7kz.png",
    "phones": [
        {
            "phoneNumber": "99999999",
            "type": "company"
        }
    ],
    "emails": [
        {
            "emailAddress": "hansara@gmail.com",
            "type": "personal"
        },
        {
            "emailAddress": "han@vtv.vn",
            "type": "company"
        }
    ]
}



GET CONTACTS (@RequestParam(name = “accountId”))
GET /contacts
(example url: http://localhost:8080/api/contacts?accountId=1)
Response successful
[
    {
        "id": 2,
        "name": "Nguyen",
        "address": "Daklak",
        "photo": "http://res.cloudinary.com/dksxh0tqy/image/upload/v1622133143/pxf4rqi1tylgvaagatix.png",
        "phones": [
            {
                "phoneNumber": "123",
                "type": "personal"
            }
        ],
        "emails": [
            {
                "emailAddress": "nguyen@gmail.com",
                "type": "personal"
            }
        ]
    },
    {
        "id": 4,
        "name": "Han Sara",
        "address": "Hàn Quốc",
        "photo": "http://res.cloudinary.com/dksxh0tqy/image/upload/v1622135859/p8r9fx0xmzcobpuka7kz.png",
        "phones": [
            {
                "phoneNumber": "99999999",
                "type": "company"
            }
        ],
        "emails": [
            {
                "emailAddress": "hansara@gmail.com",
                "type": "personal"
            },
            {
                "emailAddress": "han@vtv.vn",
                "type": "company"
            }
        ]
    }
]



GET CONTACT
GET /contacts/{id}
response successful
{
    "id": 2,
    "name": "Nguyen",
    "address": "Daklak",
    "photo": "http://res.cloudinary.com/dksxh0tqy/image/upload/v1622133143/pxf4rqi1tylgvaagatix.png",
    "phones": [
        {
            "phoneNumber": "123",
            "type": "personal"
        }
    ],
    "emails": [
        {
            "emailAddress": "nguyen@gmail.com",
            "type": "personal"
        }
    ]
}

response not found
{
    "status": "NOT_FOUND",
    "message": "Không tìm thấy contact có id=1"
}	



DELETE CONTACT
DELETE /contacts/{id}

