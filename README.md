# JobBridge

JobBridge is a job listing platform where employers can post job openings, and job seekers can discover and apply for them. This project is structured with a robust backend and a growing frontend.

## Tech Stack

- **Frontend:** React.js *(work in progress)*
- **Backend:** Spring Boot

---

## Project Status

### Backend

The backend is **fully functional** and developed following **clean code practices** and **modular architecture**. It includes:

- **Spring Security** for authentication and role-based access
- **MongoDB** as the database
- **Lombok** to reduce boilerplate code
- **Global Exception Handling** for cleaner error management
- **JWT Token-based Authentication**
- Separate entities and repositories for different user roles
- Clear and consistent code formatting

### Frontend

The frontend is currently **incomplete**, as I’m not much into JavaScript and React.  
If you're proficient in frontend development and would like to contribute – **PRs are most welcome!** 

---

## How You Can Help

- Build or improve the frontend with React.js
- Suggest design improvements or contribute to UI/UX
- Refactor or enhance backend features
- Report bugs or request features via Issues

---

## Folder Structure (Backend)

jobbridge-backend/
├── advice/ # @ControllerAdvice class
├── Config/ # Spring Security and application config
├── Controller/ # REST Controllers for different roles
├── DTO/ # Data Transfer Objects
├── Entity/ # MongoDB entities
├── Exception/ # Custom exceptions 
├── DAO/ # MongoDB repositories
├── Service/ # Business logic layer
├── Utils/ # Utility classes (e.g., JWT, mappers)
└── main.java # Application entry point

Feel free to reach out to me if you want to collaborate or need any clarification.
