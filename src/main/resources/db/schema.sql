-- Roles table
CREATE TABLE IF NOT EXISTS roles (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES ('ADMIN'), ('STUDENT')
ON CONFLICT DO NOTHING;

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  full_name VARCHAR(200) NOT NULL,
  password_hash VARCHAR(64) NOT NULL,
  role_id INTEGER NOT NULL REFERENCES roles(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Educational programs table
CREATE TABLE IF NOT EXISTS educational_programs (
  id SERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description TEXT,
  duration INTEGER NOT NULL,
  max_students INTEGER NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Modules table
CREATE TABLE IF NOT EXISTS modules (
  id SERIAL PRIMARY KEY,
  program_id INTEGER NOT NULL REFERENCES educational_programs(id) ON DELETE CASCADE,
  name VARCHAR(200) NOT NULL,
  topic VARCHAR(200),
  credits INTEGER NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Enrollments table
CREATE TABLE IF NOT EXISTS enrollments (
  id SERIAL PRIMARY KEY,
  student_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  program_id INTEGER NOT NULL REFERENCES educational_programs(id) ON DELETE CASCADE,
  enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(student_id, program_id)
);

-- Student grades table
CREATE TABLE IF NOT EXISTS student_grades (
  id SERIAL PRIMARY KEY,
  student_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  module_id INTEGER NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
  score DECIMAL(5, 2) NOT NULL,
  recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Assessment results table for analytics
CREATE TABLE IF NOT EXISTS assessment_results (
  id SERIAL PRIMARY KEY,
  enrollment_id INTEGER REFERENCES enrollments(id) ON DELETE CASCADE,
  module_id INTEGER NOT NULL REFERENCES modules(id),
  score DECIMAL(5, 2) NOT NULL,
  recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Course assignments table
CREATE TABLE IF NOT EXISTS course_assignments (
  id SERIAL PRIMARY KEY,
  program_id INTEGER NOT NULL REFERENCES educational_programs(id) ON DELETE CASCADE,
  title VARCHAR(300) NOT NULL,
  description TEXT,
  due_date DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Assignment submissions table (student answers with file)
CREATE TABLE IF NOT EXISTS assignment_submissions (
  id SERIAL PRIMARY KEY,
  assignment_id INTEGER NOT NULL REFERENCES course_assignments(id) ON DELETE CASCADE,
  student_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  file_name VARCHAR(400) NOT NULL,
  file_data BYTEA NOT NULL,
  comment TEXT,
  submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(assignment_id, student_id)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_enrollments_student ON enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_program ON enrollments(program_id);
CREATE INDEX IF NOT EXISTS idx_grades_student ON student_grades(student_id);
CREATE INDEX IF NOT EXISTS idx_grades_module ON student_grades(module_id);
CREATE INDEX IF NOT EXISTS idx_modules_program ON modules(program_id);
CREATE INDEX IF NOT EXISTS idx_assignments_program ON course_assignments(program_id);
CREATE INDEX IF NOT EXISTS idx_submissions_student ON assignment_submissions(student_id);
CREATE INDEX IF NOT EXISTS idx_submissions_assignment ON assignment_submissions(assignment_id);
