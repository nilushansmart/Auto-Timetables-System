# Smart Timetables - Frontend Web Application (Sri Lanka School Edition)

Modern React + Vite frontend for the **Multi-School Automatic Timetable System**, designed for Sri Lankan government and private schools.

---

## 🚀 Features

- **Multi-Role Authentication**: JWT-based login supporting `SCHOOL_ADMIN`, `COORDINATOR`, and `TEACHER` roles with automatic session recovery and 401 handling.
- **Interactive Timetable Viewer**:
  - **Class View**: Inspect weekly schedules for any class group (e.g. 6-A, 11-B, 12 Science).
  - **Teacher View**: Dedicated teacher schedule with a **Mobile Day-Card View** optimized for smartphones and weekly desktop matrix.
  - **Room Utilization View**: Track facility occupancy (Science Labs, Computer Labs, Classrooms).
- **Drag-and-Drop with Instant Conflict Rollback**:
  - Drag lessons across periods.
  - On **HTTP 409 Conflict**, the system immediately rolls back the lesson to its original slot and displays the exact clash reason returned by the backend engine (teacher double-booking, class overlap, room collision, or availability constraints).
  - Also includes a keyboard and touch-accessible **Move Lesson** modal dialog.
- **Master Data Management (Full CRUD with Search & Pagination)**:
  - **Teachers**: Workload constraints (max periods per day/week), subject specializations, and interactive slot availability grid (`UNAVAILABLE` / `PREFERRED`).
  - **Classes**: Grade levels (Primary, Junior Secondary, O/L, A/L), mediums (Sinhala, Tamil, English), streams, and class teacher assignments.
  - **Subjects**: Trilingual labels (English, Sinhala, Tamil) and practical/lab flags.
  - **Rooms & Labs**: Facility types and seating capacities.
  - **Time Grid Setup**: School days, period times, interval breaks, and assembly slots.
  - **Lessons & Grouping**: Periods per week, block sizes (single vs double periods), and parallel lesson groups (religion splits and O/L baskets).
- **Motion UI**: Smooth view transitions, modal dialog animations, floating toast notification stack, and interactive drag indicators powered by `motion`.

---

## 🛠️ Tech Stack

- **Framework**: React 19 (`^19.3.1`)
- **Build Tool**: Vite 6 (`^8.3.6`)
- **Styling**: Tailwind CSS v4 (`^4.3.5`) via `@tailwindcss/vite`
- **Routing**: React Router DOM v7 (`^7.18.6`)
- **HTTP Client**: Axios (`^1.21.1`)
- **Animations**: Motion (`^14.14.0`)
- **Icons**: Lucide React (`^1.77.0`)

---

## 📋 Prerequisites & Setup

### 1. Backend Server
Ensure the Spring Boot backend (`timetables-be`) is running on `http://localhost:8080`:
```powershell
# In project root
docker compose up -d
cd timetables-be
.\mvnw.cmd spring-boot:run
```

### 2. Frontend Configuration
Create or verify `.env` in `smart_timetables/`:
```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

### 3. Install & Start Development Server
```bash
npm install
npm run dev
```
The app will run at **`http://localhost:5173/`**.

---

## 🔑 Demo Credentials

| Role | Email | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **School Admin** | `admin@demoschool.lk` | `admin123` | Full administrative & timetable editing access |

*(You can also click the **"Use Demo Admin Account"** shortcut button on the login screen).*

---

## 📁 Project Architecture

```
smart_timetables/src/
├── api/                  # Axios instance with JWT & 401 interceptor
├── context/              # AuthContext & ToastContext
├── hooks/                # useAuth, useToast, usePagination, useDebounce
├── services/             # Domain API clients (auth, academic, timetable, master, teachers, lessons)
├── routes/               # AppRoutes & role-based ProtectedRoute
├── components/
│   ├── common/           # Button, Input, Select, Modal, Table, Pagination, Badge, Spinner
│   ├── layout/           # AppLayout, Navbar, Sidebar, PageHeader
│   ├── feedback/         # ErrorAlert, ToastContainer
│   └── timetable/        # TimetableGrid, TimetableSlotCell, LessonCard, ClashAlertModal, MoveEntryModal
└── pages/
    ├── auth/             # LoginPage
    ├── dashboard/        # DashboardPage
    ├── time-grid/        # TimeGridPage
    ├── teachers/         # TeacherListPage, TeacherFormModal, TeacherAvailabilityModal
    ├── classes/          # ClassListPage, ClassFormModal
    ├── subjects/         # SubjectListPage, SubjectFormModal
    ├── rooms/            # RoomListPage, RoomFormModal
    ├── lessons/          # LessonListPage, LessonFormModal, LessonGroupModal
    └── timetable/        # TimetablePage, ClassTimetableView, TeacherTimetableView, RoomTimetableView
```
