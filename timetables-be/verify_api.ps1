$ErrorActionPreference = "Stop"

function Make-Request {
    param(
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers = @{},
        [object]$Body = $null
    )
    $params = @{
        Method = $Method
        Uri = $Uri
        Headers = $Headers
        ContentType = "application/json"
    }
    if ($null -ne $Body) {
        $params["Body"] = ($Body | ConvertTo-Json -Depth 10)
    }
    try {
        return Invoke-RestMethod @params
    } catch {
        if ($_.Exception.Response) {
            $stream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            $errText = $reader.ReadToEnd()
            Write-Host "Request to $Uri failed with status $([int]$_.Exception.Response.StatusCode): $errText"
        }
        throw $_
    }
}

Write-Host "=========================================="
Write-Host "1. Testing Login..."
$loginRes = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/auth/login" -Body @{
    email = "admin@demoschool.lk"
    password = "admin123"
}
$token = $loginRes.token
$headers = @{ "Authorization" = "Bearer $token" }
Write-Host "Login successful! User: $($loginRes.user.fullName), Role: $($loginRes.user.role), School ID: $($loginRes.user.schoolId)"

Write-Host "`n2. Ensuring Academic Year 2026..."
$ayList = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/academic-years" -Headers $headers
$ay = $ayList.content | Where-Object { $_.year -eq 2026 } | Select-Object -First 1
if (-not $ay) {
    $ay = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/academic-years" -Headers $headers -Body @{
        year = 2026
        startDate = "2026-01-01"
        endDate = "2026-12-31"
        active = $true
    }
    Write-Host "Created Academic Year: ID $($ay.id), Year $($ay.year)"
} else {
    Write-Host "Found existing Academic Year: ID $($ay.id), Year $($ay.year)"
}

Write-Host "`n3. Ensuring Term 1..."
$termList = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/terms" -Headers $headers
$term = $termList.content | Where-Object { $_.academicYearId -eq $ay.id -and $_.termNo -eq 1 } | Select-Object -First 1
if (-not $term) {
    $term = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/terms" -Headers $headers -Body @{
        academicYearId = $ay.id
        termNo = 1
        startDate = "2026-01-05"
        endDate = "2026-04-10"
    }
    Write-Host "Created Term: ID $($term.id), TermNo $($term.termNo)"
} else {
    Write-Host "Found existing Term: ID $($term.id), TermNo $($term.termNo)"
}

Write-Host "`n4. Ensuring Time Slots (Monday Period 1 & 2)..."
$slots = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/time-slots" -Headers $headers
$slot1 = $slots.content | Where-Object { $_.dayOfWeek -eq 1 -and $_.periodNo -eq 1 } | Select-Object -First 1
if (-not $slot1) {
    $slot1 = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/time-slots" -Headers $headers -Body @{
        dayOfWeek = 1
        periodNo = 1
        startTime = "07:50:00"
        endTime = "08:30:00"
        slotType = "TEACHING"
    }
    Write-Host "Created Slot 1: ID $($slot1.id)"
} else {
    Write-Host "Found existing Slot 1: ID $($slot1.id)"
}

$slot2 = $slots.content | Where-Object { $_.dayOfWeek -eq 1 -and $_.periodNo -eq 2 } | Select-Object -First 1
if (-not $slot2) {
    $slot2 = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/time-slots" -Headers $headers -Body @{
        dayOfWeek = 1
        periodNo = 2
        startTime = "08:30:00"
        endTime = "09:10:00"
        slotType = "TEACHING"
    }
    Write-Host "Created Slot 2: ID $($slot2.id)"
} else {
    Write-Host "Found existing Slot 2: ID $($slot2.id)"
}

Write-Host "`n5. Ensuring Grade 6..."
$grades = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/grades" -Headers $headers
$grade = $grades.content | Where-Object { $_.gradeNo -eq 6 } | Select-Object -First 1
if (-not $grade) {
    $grade = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/grades" -Headers $headers -Body @{
        gradeNo = 6
        level = "JUNIOR_SECONDARY"
    }
    Write-Host "Created Grade: ID $($grade.id), GradeNo $($grade.gradeNo)"
} else {
    Write-Host "Found existing Grade: ID $($grade.id)"
}

Write-Host "`n6. Ensuring Class 6-A..."
$classes = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/classes?academicYearId=$($ay.id)" -Headers $headers
$cls = $classes | Where-Object { $_.name -eq "6-A" } | Select-Object -First 1
if (-not $cls) {
    $cls = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/classes" -Headers $headers -Body @{
        academicYearId = $ay.id
        gradeId = $grade.id
        mediumId = 1
        name = "6-A"
        studentCount = 40
    }
    Write-Host "Created Class: ID $($cls.id), Name $($cls.name)"
} else {
    Write-Host "Found existing Class: ID $($cls.id)"
}

Write-Host "`n7. Ensuring Subjects (MATH6 & SCI6)..."
$subjects = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/subjects" -Headers $headers
$subMath = $subjects.content | Where-Object { $_.code -eq "MATH6" } | Select-Object -First 1
if (-not $subMath) {
    $subMath = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/subjects" -Headers $headers -Body @{
        code = "MATH6"
        nameEn = "Mathematics"
        nameSi = "ගණිතය"
        nameTa = "கணிதம்"
        isReligion = $false
        isPractical = $false
    }
    Write-Host "Created Subject: ID $($subMath.id), Code $($subMath.code)"
} else {
    Write-Host "Found existing Subject Math: ID $($subMath.id)"
}

$subSci = $subjects.content | Where-Object { $_.code -eq "SCI6" } | Select-Object -First 1
if (-not $subSci) {
    $subSci = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/subjects" -Headers $headers -Body @{
        code = "SCI6"
        nameEn = "Science"
        nameSi = "විද්‍යාව"
        nameTa = "அறிவியல்"
        isReligion = $false
        isPractical = $true
    }
    Write-Host "Created Subject: ID $($subSci.id), Code $($subSci.code)"
} else {
    Write-Host "Found existing Subject Science: ID $($subSci.id)"
}

Write-Host "`n8. Ensuring Room (Room 101)..."
$rooms = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/rooms" -Headers $headers
$room = $rooms.content | Where-Object { $_.name -eq "Room 101" } | Select-Object -First 1
if (-not $room) {
    $room = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/rooms" -Headers $headers -Body @{
        name = "Room 101"
        type = "CLASSROOM"
        capacity = 45
    }
    Write-Host "Created Room: ID $($room.id), Name $($room.name)"
} else {
    Write-Host "Found existing Room: ID $($room.id)"
}

Write-Host "`n9. Ensuring Teacher (Sunil Perera)..."
$teachers = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/teachers" -Headers $headers
$teacher = $teachers.content | Where-Object { $_.fullName -eq "Sunil Perera" } | Select-Object -First 1
if (-not $teacher) {
    $teacher = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/teachers" -Headers $headers -Body @{
        staffNo = "ST-001"
        fullName = "Sunil Perera"
        shortCode = "SP"
        email = "sunil@demoschool.lk"
        phone = "0711234567"
        maxPeriodsWeek = 30
        maxPeriodsDay = 6
        active = $true
    }
    Write-Host "Created Teacher: ID $($teacher.id), Name $($teacher.fullName)"
} else {
    Write-Host "Found existing Teacher: ID $($teacher.id)"
}

Write-Host "Assigning Subject MATH6 to Teacher..."
try {
    $tsRes = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/teachers/$($teacher.id)/subjects" -Headers $headers -Body @{
        subjectId = $subMath.id
        mediumId = 1
    }
    Write-Host "Assigned Subject MATH6 to Teacher $($teacher.id)"
} catch {
    Write-Host "Teacher subject qualification already exists or assigned."
}

Write-Host "Setting Teacher Availability for Slot 1 (PREFERRED)..."
try {
    $avail1 = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/teachers/$($teacher.id)/availability" -Headers $headers -Body @{
        timeSlotId = $slot1.id
        kind = "PREFERRED"
    }
    Write-Host "Teacher availability configured."
} catch {
    Write-Host "Teacher availability already configured."
}

Write-Host "`n10. Ensuring Lessons (Math and Science with same teacher)..."
$lessons = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/lessons?academicYearId=$($ay.id)" -Headers $headers
$lessonMath = $lessons | Where-Object { $_.subject.id -eq $subMath.id -and $_.teacher.id -eq $teacher.id } | Select-Object -First 1
if (-not $lessonMath) {
    $lessonMath = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/lessons" -Headers $headers -Body @{
        academicYearId = $ay.id
        subjectId = $subMath.id
        teacherId = $teacher.id
        classGroupIds = @($cls.id)
        periodsPerWeek = 6
        blockSize = 1
        maxPerDay = 2
    }
    Write-Host "Created Math Lesson: ID $($lessonMath.id)"
} else {
    Write-Host "Found existing Math Lesson: ID $($lessonMath.id)"
}

$lessonSci = $lessons | Where-Object { $_.subject.id -eq $subSci.id -and $_.teacher.id -eq $teacher.id } | Select-Object -First 1
if (-not $lessonSci) {
    $lessonSci = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/lessons" -Headers $headers -Body @{
        academicYearId = $ay.id
        subjectId = $subSci.id
        teacherId = $teacher.id
        classGroupIds = @($cls.id)
        periodsPerWeek = 6
        blockSize = 1
        maxPerDay = 2
    }
    Write-Host "Created Science Lesson: ID $($lessonSci.id)"
} else {
    Write-Host "Found existing Science Lesson: ID $($lessonSci.id)"
}

Write-Host "`n11. Ensuring Timetable Version..."
$versions = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/timetable-versions?termId=$($term.id)" -Headers $headers
$version = $versions | Select-Object -First 1
if (-not $version) {
    $version = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/timetable-versions" -Headers $headers -Body @{
        termId = $term.id
        name = "2026 Term 1 Master Schedule v1"
    }
    Write-Host "Created Timetable Version: ID $($version.id), Name $($version.name)"
} else {
    Write-Host "Found existing Timetable Version: ID $($version.id)"
}

# Clean any existing entries in this version to test clean placement
$existingEntries = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries/by-class/$($cls.id)" -Headers $headers
foreach ($entry in $existingEntries) {
    Make-Request -Method Delete -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries/$($entry.id)" -Headers $headers
}

Write-Host "`n12. Placing Math Lesson into Slot 1, Room 101..."
$entry1 = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries" -Headers $headers -Body @{
    lessonId = $lessonMath.id
    timeSlotId = $slot1.id
    roomId = $room.id
    isPinned = $false
}
Write-Host "Successfully placed Entry 1: ID $($entry1.id) in Slot $($entry1.timeSlotId), Room $($entry1.roomId)"

Write-Host "`n13. Moving Entry 1 to Slot 2..."
$entry1Updated = Make-Request -Method Put -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries/$($entry1.id)" -Headers $headers -Body @{
    lessonId = $lessonMath.id
    timeSlotId = $slot2.id
    roomId = $room.id
    isPinned = $false
}
Write-Host "Successfully moved Entry 1: ID $($entry1Updated.id) to Slot $($entry1Updated.timeSlotId)"

Write-Host "`n14. Moving Entry 1 back to Slot 1..."
$entry1Back = Make-Request -Method Put -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries/$($entry1.id)" -Headers $headers -Body @{
    lessonId = $lessonMath.id
    timeSlotId = $slot1.id
    roomId = $room.id
    isPinned = $false
}
Write-Host "Successfully moved Entry 1 back to Slot $($entry1Back.timeSlotId)"

Write-Host "`n15. Testing Deliberate Double-Booking Clash (Teacher Sunil in Slot 1)..."
$clashCaught = $false
try {
    $clashEntry = Make-Request -Method Post -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries" -Headers $headers -Body @{
        lessonId = $lessonSci.id
        timeSlotId = $slot1.id
        roomId = $room.id
        isPinned = $false
    }
} catch {
    $clashCaught = $true
    $statusCode = [int]$_.Exception.Response.StatusCode
    if ($statusCode -eq 409) {
        Write-Host "VERIFIED: Clash successfully detected with HTTP 409 Conflict!"
    } else {
        throw "Unexpected status code: $statusCode"
    }
}
if (-not $clashCaught) {
    throw "ERROR: Clash was NOT detected when it should have been double-booked!"
}

Write-Host "`n16. Testing Reading Timetable Views..."
$classView = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries/by-class/$($cls.id)" -Headers $headers
Write-Host "Class Timetable View entries count: $($classView.Count)"
$teacherView = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries/by-teacher/$($teacher.id)" -Headers $headers
Write-Host "Teacher Timetable View entries count: $($teacherView.Count)"
$roomView = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/timetable-versions/$($version.id)/entries/by-room/$($room.id)" -Headers $headers
Write-Host "Room Timetable View entries count: $($roomView.Count)"

Write-Host "`n17. Testing Multi-Tenant Isolation (RLS)..."
# Seed School 2 and a teacher in School 2 via direct DB insert
docker exec timetables-postgres psql -U postgres -d timetable_db -c "
INSERT INTO school (id, census_no, name, type, province, zone, district, timezone, active, created_at)
VALUES (2, 'CEN-2026-999', 'Royal College', 'GOV_1AB', 'Western', 'Colombo', 'Colombo', 'Asia/Colombo', true, now())
ON CONFLICT (id) DO NOTHING;
INSERT INTO teacher (id, school_id, staff_no, full_name, short_code, email, active)
VALUES (999, 2, 'RC-001', 'School Two Teacher', 'STT', 'teacher@school2.lk', true)
ON CONFLICT (id) DO NOTHING;
" | Out-Null

# School 1 admin listing teachers
$school1Teachers = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/teachers" -Headers $headers
$crossTenantTeacherInList = $school1Teachers.content | Where-Object { $_.id -eq 999 }
if ($crossTenantTeacherInList) {
    throw "SECURITY VIOLATION: School 1 was able to see School 2's teacher in list!"
}
Write-Host "VERIFIED: School 2 teacher (ID 999) is NOT visible in School 1 teacher list."

# School 1 admin directly querying School 2 teacher ID
$isolationPassed = $false
try {
    $directQuery = Make-Request -Method Get -Uri "http://localhost:8080/api/v1/teachers/999" -Headers $headers
} catch {
    $statusCode = [int]$_.Exception.Response.StatusCode
    if ($statusCode -eq 404) {
        $isolationPassed = $true
        Write-Host "VERIFIED: Direct GET /api/v1/teachers/999 returned HTTP 404 (Row-Level Security blocked cross-tenant access)!"
    } else {
        throw "Unexpected status code: $statusCode"
    }
}
if (-not $isolationPassed) {
    throw "SECURITY VIOLATION: School 1 was able to access School 2 teacher directly!"
}

Write-Host "`n=========================================="
Write-Host "ALL REST API, CLASH DETECTION, AND MULTI-TENANT ISOLATION TESTS PASSED 100%!"
Write-Host "=========================================="
