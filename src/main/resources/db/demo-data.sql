-- Demo data for testing
-- Admin user (password: admin123)
-- Hash: SHA256("admin123") = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9

INSERT INTO users(username, full_name, password_hash, role_id) 
VALUES ('admin', 'Administrator', 
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        (SELECT id FROM roles WHERE name = 'ADMIN'))
ON CONFLICT (username) DO UPDATE
SET full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    role_id = EXCLUDED.role_id;

-- Demo student user (password: student123)
-- Hash: SHA256("student123") = 703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b

INSERT INTO users(username, full_name, password_hash, role_id)
VALUES ('student', 'Test Student',
    '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b',
        (SELECT id FROM roles WHERE name = 'STUDENT'))
ON CONFLICT (username) DO UPDATE
SET full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    role_id = EXCLUDED.role_id;

-- Repair demo student names if they were previously inserted with broken encoding (????)
UPDATE users
SET full_name = U&'\0418\0432\0430\043D \0418\0432\0430\043D\043E\0432'
WHERE username = 'ivan';

UPDATE users
SET full_name = U&'\041F\0451\0442\0440 \041F\0435\0442\0440\043E\0432'
WHERE username = 'petr';

UPDATE users
SET full_name = U&'\041E\043B\044C\0433\0430 \0421\043C\0438\0440\043D\043E\0432\0430'
WHERE username = 'olga';

UPDATE users
SET full_name = U&'\041C\0430\0440\0438\044F \041A\0443\0437\043D\0435\0446\043E\0432\0430'
WHERE username = 'maria';

-- Demo educational program
INSERT INTO educational_programs(name, description, duration, max_students)
SELECT 'Python Basics', 'Introduction to Python programming', 30, 50
WHERE NOT EXISTS (
    SELECT 1 FROM educational_programs WHERE name = 'Python Basics'
);

-- Demo modules
INSERT INTO modules(program_id, name, topic, credits)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Python Basics' ORDER BY id LIMIT 1),
    'Variables & Data Types',
    'Introduction to variables and basic data types',
    3
WHERE NOT EXISTS (
    SELECT 1 FROM modules WHERE name = 'Variables & Data Types'
);

INSERT INTO modules(program_id, name, topic, credits)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Python Basics' ORDER BY id LIMIT 1),
    'Control Flow',
    'If statements, loops, and control structures',
    3
WHERE NOT EXISTS (
    SELECT 1 FROM modules WHERE name = 'Control Flow'
);

-- Demo enrollment
INSERT INTO enrollments(student_id, program_id)
VALUES (
    (SELECT id FROM users WHERE username = 'student'),
    (SELECT id FROM educational_programs WHERE name = 'Python Basics')
)
ON CONFLICT DO NOTHING;

-- Demo grades
INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'student'),
    (SELECT id FROM modules WHERE name = 'Variables & Data Types'),
    85.5
)
ON CONFLICT DO NOTHING;

INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'student'),
    (SELECT id FROM modules WHERE name = 'Control Flow'),
    78.0
)
ON CONFLICT DO NOTHING;

-- Demo assignments
INSERT INTO course_assignments(program_id, title, description, due_date)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Python Basics' ORDER BY id LIMIT 1),
    'Задание 1: Типы данных',
    'Напишите программу, демонстрирующую работу с различными типами данных Python. Загрузите файл .py',
    CURRENT_DATE + INTERVAL '7 days'
WHERE NOT EXISTS (
    SELECT 1 FROM course_assignments WHERE title = 'Задание 1: Типы данных'
);

INSERT INTO course_assignments(program_id, title, description, due_date)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Python Basics' ORDER BY id LIMIT 1),
    'Задание 2: Управление потоком',
    'Реализуйте алгоритм сортировки пузырьком с использованием циклов и условий. Загрузите файл .py',
    CURRENT_DATE + INTERVAL '14 days'
WHERE NOT EXISTS (
    SELECT 1 FROM course_assignments WHERE title = 'Задание 2: Управление потоком'
);

-- ============================================================
-- Additional demo students (password: student123 for all)
-- SHA256("student123") = 703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b
-- ============================================================

INSERT INTO users(username, full_name, password_hash, role_id)
VALUES ('ivan', U&'\0418\0432\0430\043D \0418\0432\0430\043D\043E\0432',
    '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b',
    (SELECT id FROM roles WHERE name = 'STUDENT'))
ON CONFLICT (username) DO UPDATE
SET full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    role_id = EXCLUDED.role_id;

INSERT INTO users(username, full_name, password_hash, role_id)
VALUES ('petr', U&'\041F\0451\0442\0440 \041F\0435\0442\0440\043E\0432',
    '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b',
    (SELECT id FROM roles WHERE name = 'STUDENT'))
ON CONFLICT (username) DO UPDATE
SET full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    role_id = EXCLUDED.role_id;

INSERT INTO users(username, full_name, password_hash, role_id)
VALUES ('olga', U&'\041E\043B\044C\0433\0430 \0421\043C\0438\0440\043D\043E\0432\0430',
    '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b',
    (SELECT id FROM roles WHERE name = 'STUDENT'))
ON CONFLICT (username) DO UPDATE
SET full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    role_id = EXCLUDED.role_id;

INSERT INTO users(username, full_name, password_hash, role_id)
VALUES ('maria', U&'\041C\0430\0440\0438\044F \041A\0443\0437\043D\0435\0446\043E\0432\0430',
    '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b',
    (SELECT id FROM roles WHERE name = 'STUDENT'))
ON CONFLICT (username) DO UPDATE
SET full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    role_id = EXCLUDED.role_id;

-- ============================================================
-- Second educational program: Web Development
-- ============================================================

INSERT INTO educational_programs(name, description, duration, max_students)
SELECT 'Web Development', U&'\0412\0432\0435\0434\0435\043D\0438\0435 \0432 \0432\0435\0431-\0440\0430\0437\0440\0430\0431\043E\0442\043A\0443: HTML, CSS \0438 JavaScript', 45, 40
WHERE NOT EXISTS (
    SELECT 1 FROM educational_programs WHERE name = 'Web Development'
);

INSERT INTO modules(program_id, name, topic, credits)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Web Development' ORDER BY id LIMIT 1),
    'HTML & CSS Basics',
    U&'\0421\0442\0440\0443\043A\0442\0443\0440\0430 \0441\0442\0440\0430\043D\0438\0446\044B, \0441\0442\0438\043B\0438, \0432\0435\0440\0441\0442\043A\0430 \0438 \0448\0440\0438\0444\0442\044B',
    4
WHERE NOT EXISTS (
    SELECT 1 FROM modules WHERE name = 'HTML & CSS Basics'
);

INSERT INTO modules(program_id, name, topic, credits)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Web Development' ORDER BY id LIMIT 1),
    'JavaScript Fundamentals',
    U&'\0414\043E\043C, \0441\043E\0431\044B\0442\0438\044F, \0444\0443\043D\043A\0446\0438\0438 \0438 \0437\0430\043F\0440\043E\0441\044B Fetch',
    5
WHERE NOT EXISTS (
    SELECT 1 FROM modules WHERE name = 'JavaScript Fundamentals'
);

-- ============================================================
-- Additional assignment for Python Basics
-- ============================================================

INSERT INTO course_assignments(program_id, title, description, due_date)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Python Basics' ORDER BY id LIMIT 1),
    U&'\0417\0430\0434\0430\043D\0438\0435 3: \0424\0443\043D\043A\0446\0438\0438 \0438 \043C\043E\0434\0443\043B\0438',
    U&'\041D\0430\043F\0438\0448\0438\0442\0435 \043D\0430\0431\043E\0440 \0444\0443\043D\043A\0446\0438\0439 \0434\043B\044F \0440\0430\0431\043E\0442\044B \0441\043E \0441\043F\0438\0441\043A\0430\043C\0438 \0438 \0441\043B\043E\0432\0430\0440\044F\043C\0438, \043E\0444\043E\0440\043C\0438\0442\0435 \0432 \043E\0442\0434\0435\043B\044C\043D\044B\0439 \043C\043E\0434\0443\043B\044C. \0417\0430\0433\0440\0443\0437\0438\0442\0435 \0444\0430\0439\043B .py',
    CURRENT_DATE + INTERVAL '21 days'
WHERE NOT EXISTS (
    SELECT 1 FROM course_assignments WHERE title = U&'\0417\0430\0434\0430\043D\0438\0435 3: \0424\0443\043D\043A\0446\0438\0438 \0438 \043C\043E\0434\0443\043B\0438'
);

-- Assignments for Web Development
INSERT INTO course_assignments(program_id, title, description, due_date)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Web Development' ORDER BY id LIMIT 1),
    U&'\0417\0430\0434\0430\043D\0438\0435 1: \0412\0435\0440\0441\0442\043A\0430 \0441\0442\0440\0430\043D\0438\0446\044B',
    U&'\0421\043E\0437\0434\0430\0439\0442\0435 HTML-\0441\0442\0440\0430\043D\0438\0446\0443 \0441 \0437\0430\0433\043E\043B\043E\0432\043A\043E\043C, \043C\0435\043D\044E \043D\0430\0432\0438\0433\0430\0446\0438\0438 \0438 \0441\0442\0438\043B\0438\0437\0430\0446\0438\0435\0439 CSS. \0417\0430\0433\0440\0443\0437\0438\0442\0435 \0430\0440\0445\0438\0432 .zip',
    CURRENT_DATE + INTERVAL '10 days'
WHERE NOT EXISTS (
    SELECT 1 FROM course_assignments WHERE title = U&'\0417\0430\0434\0430\043D\0438\0435 1: \0412\0435\0440\0441\0442\043A\0430 \0441\0442\0440\0430\043D\0438\0446\044B'
);

INSERT INTO course_assignments(program_id, title, description, due_date)
SELECT
    (SELECT id FROM educational_programs WHERE name = 'Web Development' ORDER BY id LIMIT 1),
    U&'\0417\0430\0434\0430\043D\0438\0435 2: \0418\043D\0442\0435\0440\0430\043A\0442\0438\0432\043D\044B\0439 \0441\043F\0438\0441\043E\043A',
    U&'\0420\0435\0430\043B\0438\0437\0443\0439\0442\0435 \0441\043F\0438\0441\043E\043A \0437\0430\0434\0430\0447 \0441 \0432\043E\0437\043C\043E\0436\043D\043E\0441\0442\044C\044E \0434\043E\0431\0430\0432\043B\0435\043D\0438\044F, \0440\0435\0434\0430\043A\0442\0438\0440\043E\0432\0430\043D\0438\044F \0438 \0443\0434\0430\043B\0435\043D\0438\044F \044D\043B\0435\043C\0435\043D\0442\043E\0432 \043D\0430 JavaScript. \0417\0430\0433\0440\0443\0437\0438\0442\0435 \0430\0440\0445\0438\0432 .zip',
    CURRENT_DATE + INTERVAL '20 days'
WHERE NOT EXISTS (
    SELECT 1 FROM course_assignments WHERE title = U&'\0417\0430\0434\0430\043D\0438\0435 2: \0418\043D\0442\0435\0440\0430\043A\0442\0438\0432\043D\044B\0439 \0441\043F\0438\0441\043E\043A'
);

-- ============================================================
-- Enroll additional students into Python Basics
-- ============================================================

INSERT INTO enrollments(student_id, program_id)
VALUES (
    (SELECT id FROM users WHERE username = 'ivan'),
    (SELECT id FROM educational_programs WHERE name = 'Python Basics')
) ON CONFLICT DO NOTHING;

INSERT INTO enrollments(student_id, program_id)
VALUES (
    (SELECT id FROM users WHERE username = 'petr'),
    (SELECT id FROM educational_programs WHERE name = 'Python Basics')
) ON CONFLICT DO NOTHING;

INSERT INTO enrollments(student_id, program_id)
VALUES (
    (SELECT id FROM users WHERE username = 'olga'),
    (SELECT id FROM educational_programs WHERE name = 'Python Basics')
) ON CONFLICT DO NOTHING;

INSERT INTO enrollments(student_id, program_id)
VALUES (
    (SELECT id FROM users WHERE username = 'maria'),
    (SELECT id FROM educational_programs WHERE name = 'Python Basics')
) ON CONFLICT DO NOTHING;

-- Enroll students into Web Development
INSERT INTO enrollments(student_id, program_id)
VALUES (
    (SELECT id FROM users WHERE username = 'ivan'),
    (SELECT id FROM educational_programs WHERE name = 'Web Development')
) ON CONFLICT DO NOTHING;

INSERT INTO enrollments(student_id, program_id)
VALUES (
    (SELECT id FROM users WHERE username = 'olga'),
    (SELECT id FROM educational_programs WHERE name = 'Web Development')
) ON CONFLICT DO NOTHING;

-- ============================================================
-- Grades for additional students
-- ============================================================

INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'ivan'),
    (SELECT id FROM modules WHERE name = 'Variables & Data Types'),
    92.0
) ON CONFLICT DO NOTHING;

INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'ivan'),
    (SELECT id FROM modules WHERE name = 'Control Flow'),
    88.5
) ON CONFLICT DO NOTHING;

INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'petr'),
    (SELECT id FROM modules WHERE name = 'Variables & Data Types'),
    74.0
) ON CONFLICT DO NOTHING;

INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'petr'),
    (SELECT id FROM modules WHERE name = 'Control Flow'),
    65.5
) ON CONFLICT DO NOTHING;

INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'olga'),
    (SELECT id FROM modules WHERE name = 'Variables & Data Types'),
    97.5
) ON CONFLICT DO NOTHING;

INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'olga'),
    (SELECT id FROM modules WHERE name = 'Control Flow'),
    95.0
) ON CONFLICT DO NOTHING;

INSERT INTO student_grades(student_id, module_id, score)
VALUES (
    (SELECT id FROM users WHERE username = 'maria'),
    (SELECT id FROM modules WHERE name = 'Variables & Data Types'),
    81.0
) ON CONFLICT DO NOTHING;

-- ============================================================
-- Assignment submissions (student answers with file content)
-- ============================================================

-- student: submission for Assignment 1 (Types)
INSERT INTO assignment_submissions(assignment_id, student_id, file_name, file_data, comment, submitted_at)
SELECT
    (SELECT id FROM course_assignments WHERE title = 'Задание 1: Типы данных'),
    (SELECT id FROM users WHERE username = 'student'),
    'task1_types.py',
    convert_to(
        'x = 42' || chr(10) ||
        'pi = 3.14' || chr(10) ||
        'name = "Test Student"' || chr(10) ||
        'flag = True' || chr(10) ||
        'data = [1, 2, 3]' || chr(10) ||
        'print(type(x), type(pi), type(name), type(flag), type(data))',
        'UTF8'),
    U&'\0412\044B\043F\043E\043B\043D\0435\043D\043E \0432\0441\0435 \043F\0443\043D\043A\0442\044B \0437\0430\0434\0430\043D\0438\044F',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
WHERE NOT EXISTS (
    SELECT 1 FROM assignment_submissions
    WHERE assignment_id = (SELECT id FROM course_assignments WHERE title = 'Задание 1: Типы данных')
      AND student_id     = (SELECT id FROM users WHERE username = 'student')
);

-- student: submission for Assignment 2 (Control Flow)
INSERT INTO assignment_submissions(assignment_id, student_id, file_name, file_data, comment, submitted_at)
SELECT
    (SELECT id FROM course_assignments WHERE title = 'Задание 2: Управление потоком'),
    (SELECT id FROM users WHERE username = 'student'),
    'task2_bubble_sort.py',
    convert_to(
        'def bubble_sort(arr):' || chr(10) ||
        '    n = len(arr)' || chr(10) ||
        '    for i in range(n):' || chr(10) ||
        '        for j in range(0, n - i - 1):' || chr(10) ||
        '            if arr[j] > arr[j + 1]:' || chr(10) ||
        '                arr[j], arr[j + 1] = arr[j + 1], arr[j]' || chr(10) ||
        '    return arr' || chr(10) ||
        '' || chr(10) ||
        'print(bubble_sort([64, 34, 25, 12, 22, 11, 90]))',
        'UTF8'),
    U&'\0418\0441\043F\043E\043B\044C\0437\043E\0432\0430\043B\0430 \0432\043B\043E\0436\0435\043D\043D\044B\0435 \0446\0438\043A\043B\044B \0438 \0443\0441\043B\043E\0432\0438\044F',
    CURRENT_TIMESTAMP - INTERVAL '1 days'
WHERE NOT EXISTS (
    SELECT 1 FROM assignment_submissions
    WHERE assignment_id = (SELECT id FROM course_assignments WHERE title = 'Задание 2: Управление потоком')
      AND student_id     = (SELECT id FROM users WHERE username = 'student')
);

-- ivan: submission for Assignment 1
INSERT INTO assignment_submissions(assignment_id, student_id, file_name, file_data, comment, submitted_at)
SELECT
    (SELECT id FROM course_assignments WHERE title = 'Задание 1: Типы данных'),
    (SELECT id FROM users WHERE username = 'ivan'),
    'ivan_task1.py',
    convert_to(
        'a = 10' || chr(10) ||
        'b = 3.5' || chr(10) ||
        'c = "hello"' || chr(10) ||
        'd = (1, 2, 3)' || chr(10) ||
        'e = {"key": "value"}' || chr(10) ||
        'for v in [a, b, c, d, e]:' || chr(10) ||
        '    print(v, "->", type(v).__name__)',
        'UTF8'),
    U&'\0414\043E\0431\0430\0432\0438\043B \043F\0440\0438\043C\0435\0440\044B \0441 \043A\043E\0440\0442\0435\0436\0430\043C\0438 \0438 \0441\043B\043E\0432\0430\0440\044F\043C\0438',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
WHERE NOT EXISTS (
    SELECT 1 FROM assignment_submissions
    WHERE assignment_id = (SELECT id FROM course_assignments WHERE title = 'Задание 1: Типы данных')
      AND student_id     = (SELECT id FROM users WHERE username = 'ivan')
);

-- ivan: submission for Assignment 2
INSERT INTO assignment_submissions(assignment_id, student_id, file_name, file_data, comment, submitted_at)
SELECT
    (SELECT id FROM course_assignments WHERE title = 'Задание 2: Управление потоком'),
    (SELECT id FROM users WHERE username = 'ivan'),
    'ivan_bubble.py',
    convert_to(
        'def bubble_sort(lst):' || chr(10) ||
        '    for i in range(len(lst) - 1):' || chr(10) ||
        '        for j in range(len(lst) - 1 - i):' || chr(10) ||
        '            if lst[j] > lst[j+1]:' || chr(10) ||
        '                lst[j], lst[j+1] = lst[j+1], lst[j]' || chr(10) ||
        '    return lst' || chr(10) ||
        '' || chr(10) ||
        'nums = [5, 3, 8, 1, 9, 2]' || chr(10) ||
        'print("Sorted:", bubble_sort(nums))',
        'UTF8'),
    U&'\0420\0435\0430\043B\0438\0437\043E\0432\0430\043B \0447\0435\0440\0435\0437 \043E\0442\0434\0435\043B\044C\043D\0443\044E \0444\0443\043D\043A\0446\0438\044E',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
WHERE NOT EXISTS (
    SELECT 1 FROM assignment_submissions
    WHERE assignment_id = (SELECT id FROM course_assignments WHERE title = 'Задание 2: Управление потоком')
      AND student_id     = (SELECT id FROM users WHERE username = 'ivan')
);

-- olga: submission for Assignment 1
INSERT INTO assignment_submissions(assignment_id, student_id, file_name, file_data, comment, submitted_at)
SELECT
    (SELECT id FROM course_assignments WHERE title = 'Задание 1: Типы данных'),
    (SELECT id FROM users WHERE username = 'olga'),
    'olga_types.py',
    convert_to(
        '# Python data types demo' || chr(10) ||
        'integer_val  = 100' || chr(10) ||
        'float_val    = 2.718' || chr(10) ||
        'complex_val  = 3 + 4j' || chr(10) ||
        'string_val   = "Python"' || chr(10) ||
        'bool_val     = False' || chr(10) ||
        'list_val     = [1, "two", 3.0]' || chr(10) ||
        'tuple_val    = (4, 5, 6)' || chr(10) ||
        'set_val      = {7, 8, 9}' || chr(10) ||
        'dict_val     = {"lang": "Python", "version": 3}' || chr(10) ||
        '' || chr(10) ||
        'for name, val in locals().items():' || chr(10) ||
        '    print(f"{name}: {type(val).__name__} = {val}")',
        'UTF8'),
    U&'\0420\0430\0441\0441\043C\043E\0442\0440\0435\043B\0430 \0432\0441\0435 \0432\0441\0442\0440\043E\0435\043D\043D\044B\0435 \0442\0438\043F\044B, \0432\043A\043B\044E\0447\0430\044F \0441\043B\043E\0436\043D\044B\0435',
    CURRENT_TIMESTAMP - INTERVAL '4 days'
WHERE NOT EXISTS (
    SELECT 1 FROM assignment_submissions
    WHERE assignment_id = (SELECT id FROM course_assignments WHERE title = 'Задание 1: Типы данных')
      AND student_id     = (SELECT id FROM users WHERE username = 'olga')
);

-- olga: submission for Assignment 2
INSERT INTO assignment_submissions(assignment_id, student_id, file_name, file_data, comment, submitted_at)
SELECT
    (SELECT id FROM course_assignments WHERE title = 'Задание 2: Управление потоком'),
    (SELECT id FROM users WHERE username = 'olga'),
    'olga_sort.py',
    convert_to(
        'def bubble_sort(arr):' || chr(10) ||
        '    n = len(arr)' || chr(10) ||
        '    for i in range(n - 1):' || chr(10) ||
        '        swapped = False' || chr(10) ||
        '        for j in range(n - 1 - i):' || chr(10) ||
        '            if arr[j] > arr[j + 1]:' || chr(10) ||
        '                arr[j], arr[j + 1] = arr[j + 1], arr[j]' || chr(10) ||
        '                swapped = True' || chr(10) ||
        '        if not swapped:' || chr(10) ||
        '            break  # early exit optimisation' || chr(10) ||
        '    return arr' || chr(10) ||
        '' || chr(10) ||
        'print(bubble_sort([38, 27, 43, 3, 9, 82, 10]))',
        'UTF8'),
    U&'\041E\043F\0442\0438\043C\0438\0437\0438\0440\043E\0432\0430\043B\0430 \0430\043B\0433\043E\0440\0438\0442\043C: \0440\0430\043D\043D\0438\0439 \0432\044B\0445\043E\0434 \043F\0440\0438 \043E\0442\0441\0443\0442\0441\0442\0432\0438\0438 \043F\0435\0440\0435\0441\0442\0430\043D\043E\0432\043E\043A',
    CURRENT_TIMESTAMP - INTERVAL '6 hours'
WHERE NOT EXISTS (
    SELECT 1 FROM assignment_submissions
    WHERE assignment_id = (SELECT id FROM course_assignments WHERE title = 'Задание 2: Управление потоком')
      AND student_id     = (SELECT id FROM users WHERE username = 'olga')
);

-- maria: submission for Assignment 1 only
INSERT INTO assignment_submissions(assignment_id, student_id, file_name, file_data, comment, submitted_at)
SELECT
    (SELECT id FROM course_assignments WHERE title = 'Задание 1: Типы данных'),
    (SELECT id FROM users WHERE username = 'maria'),
    'maria_task1.py',
    convert_to(
        'print(type(1))' || chr(10) ||
        'print(type(1.5))' || chr(10) ||
        'print(type("hello"))' || chr(10) ||
        'print(type(True))' || chr(10) ||
        'print(type([]))' || chr(10) ||
        'print(type({}))',
        'UTF8'),
    U&'\041E\0442\0432\0435\0442\0438\043B\0430 \043D\0430 \043E\0441\043D\043E\0432\043D\044B\0435 \0442\0438\043F\044B',
    CURRENT_TIMESTAMP - INTERVAL '7 days'
WHERE NOT EXISTS (
    SELECT 1 FROM assignment_submissions
    WHERE assignment_id = (SELECT id FROM course_assignments WHERE title = 'Задание 1: Типы данных')
      AND student_id     = (SELECT id FROM users WHERE username = 'maria')
);
