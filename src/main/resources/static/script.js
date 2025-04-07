// API Base URL - using relative URL to avoid hardcoding the port
const API_URL = '/api/students';

/**
 * Show alert message
 * @param {string} message - Message to display
 * @param {string} type - Alert type (success, danger, warning, info)
 */
function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.setAttribute('role', 'alert');

    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    document.body.appendChild(alertDiv);

    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => alertDiv.remove(), 300);
    }, 5000);
}

// DOM Elements
const studentForm = document.getElementById('studentForm');
const formTitle = document.getElementById('formTitle');
const studentId = document.getElementById('studentId');
const nameInput = document.getElementById('name');
const birthDateInput = document.getElementById('birthDate');
const mobileNoInput = document.getElementById('mobileNo');
const photoInput = document.getElementById('photo');
const photoPreview = document.getElementById('photoPreview');
const photoPreviewContainer = document.getElementById('photoPreviewContainer');
const removePhotoBtn = document.getElementById('removePhotoBtn');
const saveBtn = document.getElementById('saveBtn');
const cancelBtn = document.getElementById('cancelBtn');
const studentTableBody = document.getElementById('studentTableBody');
const searchInput = document.getElementById('searchInput');
const searchBtn = document.getElementById('searchBtn');
const clearSearchBtn = document.getElementById('clearSearchBtn');
const paginationContainer = document.getElementById('paginationContainer');

// Photo variables
let currentPhotoBase64 = null;

// Pagination variables
let currentPage = 0;
let totalPages = 0;
let pageSize = 5;
let currentSearchTerm = '';

// Store all students for client-side pagination
let allLoadedStudents = [];
let filteredStudents = [];

// Load all students when page loads
document.addEventListener('DOMContentLoaded', loadStudents);

// Event Listeners
studentForm.addEventListener('submit', saveStudent);
cancelBtn.addEventListener('click', resetForm);
searchBtn.addEventListener('click', searchStudents);
clearSearchBtn.addEventListener('click', clearSearch);
photoInput.addEventListener('change', handlePhotoChange);
removePhotoBtn.addEventListener('click', removePhoto);

/**
 * Load all students
 * @param {number} page - Page number (0-based)
 */
function loadStudents(page = 0) {
    try {
        // Show loading indicator
        studentTableBody.innerHTML = '<tr><td colspan="6" class="text-center">Loading students...</td></tr>';

        currentPage = page;
        currentSearchTerm = '';

        // Get all students
        fetch(`${API_URL}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Cache-Control': 'no-cache'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(apiResponse => {
            if (!apiResponse || !apiResponse.success) {
                throw new Error(apiResponse?.message || 'Failed to load students');
            }

            // Handle the data
            const students = apiResponse.data || [];
            allLoadedStudents = students;
            filteredStudents = [...students];

            // Apply pagination
            changePage(0);
        })
        .catch(error => {
            console.error('Error loading students:', error);
            studentTableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-danger">
                        Failed to load students: ${error.message}
                    </td>
                </tr>
            `;
            showAlert(`Failed to load students: ${error.message}`, 'danger');
        });
    } catch (error) {
        console.error('Error in loadStudents function:', error);
        showAlert('An unexpected error occurred. Please try again.', 'danger');
    }
}

/**
 * Search students by name
 */
function searchStudents() {
    const searchTerm = searchInput.value.trim();

    if (!searchTerm) {
        showAlert('Please enter a search term', 'warning');
        return;
    }

    currentSearchTerm = searchTerm;

    // Show loading indicator
    studentTableBody.innerHTML = '<tr><td colspan="6" class="text-center">Searching...</td></tr>';

    fetch(`${API_URL}/search?name=${encodeURIComponent(searchTerm)}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
    })
    .then(apiResponse => {
        if (!apiResponse || !apiResponse.success) {
            throw new Error(apiResponse?.message || 'Search failed');
        }

        const students = apiResponse.data || [];
        filteredStudents = students;

        if (students.length === 0) {
            studentTableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center">
                        No students found matching "${searchTerm}"
                    </td>
                </tr>
            `;
            paginationContainer.innerHTML = '';
        } else {
            changePage(0);
        }
    })
    .catch(error => {
        console.error('Error searching students:', error);
        studentTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-danger">
                    Search failed: ${error.message}
                </td>
            </tr>
        `;
        showAlert(`Search failed: ${error.message}`, 'danger');
    });
}

function updatePagination(currentPage, totalPages) {
    paginationContainer.innerHTML = '';

    if (totalPages <= 1) {
        return;
    }

    const ul = document.createElement('ul');
    ul.className = 'pagination justify-content-center';

    // Previous button
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
    const prevLink = document.createElement('a');
    prevLink.className = 'page-link';
    prevLink.href = 'javascript:void(0);';
    prevLink.textContent = 'Previous';
    prevLink.addEventListener('click', (e) => {
        e.preventDefault();
        if (currentPage > 0) {
            changePage(currentPage - 1);
        }
    });
    prevLi.appendChild(prevLink);
    ul.appendChild(prevLi);

    // Page numbers
    for (let i = 0; i < totalPages; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${i === currentPage ? 'active' : ''}`;
        const link = document.createElement('a');
        link.className = 'page-link';
        link.href = 'javascript:void(0);';
        link.textContent = i + 1;
        link.addEventListener('click', (e) => {
            e.preventDefault();
            changePage(i);
        });
        li.appendChild(link);
        ul.appendChild(li);
    }

    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    const nextLink = document.createElement('a');
    nextLink.className = 'page-link';
    nextLink.href = 'javascript:void(0);';
    nextLink.textContent = 'Next';
    nextLink.addEventListener('click', (e) => {
        e.preventDefault();
        if (currentPage < totalPages - 1) {
            changePage(currentPage + 1);
        }
    });
    nextLi.appendChild(nextLink);
    ul.appendChild(nextLi);

    paginationContainer.appendChild(ul);
}

// Function to change page without reloading data
function changePage(page) {
    console.log(`Changing to page ${page}`);
    currentPage = page;

    // Get the current data set based on search term
    const dataToUse = filteredStudents;

    // Calculate pagination
    const startIndex = page * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedStudents = dataToUse.slice(startIndex, endIndex);

    // Update display
    displayStudents(paginatedStudents);
    updatePagination(page, Math.ceil(dataToUse.length / pageSize) || 1);
}

function clearSearch() {
    searchInput.value = '';
    currentSearchTerm = '';
    filteredStudents = [...allLoadedStudents];
    changePage(0);
}

function displayStudents(students) {
    studentTableBody.innerHTML = '';

    if (!students || students.length === 0) {
        studentTableBody.innerHTML = '<tr><td colspan="6" class="text-center">No students found</td></tr>';
        return;
    }

    console.log(`Displaying ${students.length} students`);

    // Create a document fragment to improve performance
    const fragment = document.createDocumentFragment();

    students.forEach(student => {
        try {
            const row = document.createElement('tr');

            // Safe access to properties with fallbacks
            const id = student.id || '';
            const name = student.name || '';
            const birthDate = student.birthDate ? formatDate(student.birthDate) : '';
            const mobileNo = student.mobileNo || '';

            // Create photo cell content
            let photoCell = 'No Photo';
            if (student.photoBase64) {
                try {
                    // Limit the size of the base64 string for better performance
                    const maxLength = 100000; // Limit to 100KB
                    const photoBase64 = student.photoBase64.length > maxLength ?
                        student.photoBase64.substring(0, maxLength) : student.photoBase64;

                    photoCell = `<img src="data:image/jpeg;base64,${photoBase64}" class="img-thumbnail" style="max-height: 50px; max-width: 50px;" alt="Student Photo">`;
                } catch (error) {
                    console.error('Error processing photo:', error);
                    photoCell = 'Photo Error';
                }
            }

            // Create cells individually for better performance
            const idCell = document.createElement('td');
            idCell.textContent = id;

            const photoTd = document.createElement('td');
            photoTd.innerHTML = photoCell;

            const nameCell = document.createElement('td');
            nameCell.textContent = name;

            const birthDateCell = document.createElement('td');
            birthDateCell.textContent = birthDate;

            const mobileNoCell = document.createElement('td');
            mobileNoCell.textContent = mobileNo;

            const actionsCell = document.createElement('td');

            // Create edit button
            const editBtn = document.createElement('button');
            editBtn.className = 'btn btn-sm btn-primary btn-action edit-btn';
            editBtn.textContent = 'Edit';
            editBtn.dataset.id = id;
            editBtn.addEventListener('click', () => editStudent(id));

            // Create delete button
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'btn btn-sm btn-danger btn-action delete-btn';
            deleteBtn.textContent = 'Delete';
            deleteBtn.dataset.id = id;
            deleteBtn.addEventListener('click', () => deleteStudent(id));

            // Add buttons to actions cell
            actionsCell.appendChild(editBtn);
            actionsCell.appendChild(document.createTextNode(' ')); // Space between buttons
            actionsCell.appendChild(deleteBtn);

            // Add all cells to the row
            row.appendChild(idCell);
            row.appendChild(photoTd);
            row.appendChild(nameCell);
            row.appendChild(birthDateCell);
            row.appendChild(mobileNoCell);
            row.appendChild(actionsCell);

            // Add the row to the fragment
            fragment.appendChild(row);
        } catch (error) {
            console.error('Error creating row for student:', error, student);
        }
    });

    // Add the fragment to the table body
    studentTableBody.appendChild(fragment);

    console.log('Students displayed successfully');
}

function saveStudent(e) {
    e.preventDefault();

    try {
        const student = {
            name: nameInput.value.trim(),
            birthDate: birthDateInput.value,
            mobileNo: mobileNoInput.value.trim(),
            photoBase64: currentPhotoBase64
        };

        // Validate required fields
        if (!student.name) {
            showAlert('Name is required', 'danger');
            return;
        }

        if (!student.birthDate) {
            showAlert('Birth Date is required', 'danger');
            return;
        }

        if (!student.mobileNo) {
            showAlert('Mobile Number is required', 'danger');
            return;
        }

        // Show loading message
        showAlert('Saving student...', 'info');

        const id = studentId.value;
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_URL}/${id}` : API_URL;

        console.log(`Saving student with method ${method} to ${url}`, student);

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(student)
        })
        .then(response => {
            console.log('Save response status:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(savedStudent => {
            console.log('Student saved successfully:', savedStudent);
            resetForm();
            if (currentSearchTerm) {
                searchStudents(currentPage);
            } else {
                loadStudents(currentPage);
            }
            showAlert(id ? 'Student updated successfully!' : 'Student added successfully!', 'success');
        })
        .catch(error => {
            console.error('Error saving student:', error);
            showAlert('Failed to save student. Please try again.', 'danger');
        });
    } catch (error) {
        console.error('Error in saveStudent function:', error);
        showAlert('An unexpected error occurred. Please try again.', 'danger');
    }
}

function editStudent(id) {
    fetch(`${API_URL}/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(student => {
            if (!student || !student.id) {
                throw new Error('Invalid student data received');
            }

            // Populate form with student data
            studentId.value = student.id;
            nameInput.value = student.name || '';
            birthDateInput.value = student.birthDate ? formatDateForInput(student.birthDate) : '';
            mobileNoInput.value = student.mobileNo || '';

            // Handle photo
            if (student.photoBase64) {
                try {
                    currentPhotoBase64 = student.photoBase64;
                    photoPreview.src = `data:image/jpeg;base64,${student.photoBase64}`;
                    photoPreviewContainer.classList.remove('d-none');
                } catch (error) {
                    console.error('Error setting photo preview:', error);
                    currentPhotoBase64 = null;
                    photoPreviewContainer.classList.add('d-none');
                }
            } else {
                currentPhotoBase64 = null;
                photoPreviewContainer.classList.add('d-none');
            }

            // Update form title
            formTitle.textContent = 'Edit Student';
            saveBtn.textContent = 'Update';

            // Scroll to form
            document.querySelector('.form-container').scrollIntoView({ behavior: 'smooth' });
        })
        .catch(error => {
            console.error('Error fetching student details:', error);
            showAlert('Failed to load student details. Please try again.', 'danger');
        });
}

function deleteStudent(id) {
    if (!id) {
        showAlert('Invalid student ID', 'danger');
        return;
    }

    if (confirm('Are you sure you want to delete this student?')) {
        try {
            fetch(`${API_URL}/${id}`, {
                method: 'DELETE'
            })
            .then(response => {
                if (response.ok) {
                    if (currentSearchTerm) {
                        searchStudents(currentPage);
                    } else {
                        loadStudents(currentPage);
                    }
                    showAlert('Student deleted successfully!', 'success');
                } else {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
            })
            .catch(error => {
                console.error('Error deleting student:', error);
                showAlert('Failed to delete student. Please try again.', 'danger');
            });
        } catch (error) {
            console.error('Error in deleteStudent function:', error);
            showAlert('An unexpected error occurred. Please try again.', 'danger');
        }
    }
}

function resetForm() {
    studentForm.reset();
    studentId.value = '';
    formTitle.textContent = 'Add New Student';
    saveBtn.textContent = 'Save';
    currentPhotoBase64 = null;
    photoPreviewContainer.classList.add('d-none');
}

function handlePhotoChange(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(event) {
            const base64String = event.target.result.split(',')[1];
            currentPhotoBase64 = base64String;
            photoPreview.src = event.target.result;
            photoPreviewContainer.classList.remove('d-none');
        };
        reader.readAsDataURL(file);
    }
}

function removePhoto() {
    photoInput.value = '';
    currentPhotoBase64 = null;
    photoPreviewContainer.classList.add('d-none');
}

/**
 * Format date for display (DD/MM/YYYY)
 * @param {string} dateString - ISO date string
 * @returns {string} Formatted date string
 */
function formatDate(dateString) {
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString();
    } catch (error) {
        console.error('Error formatting date:', error);
        return dateString || '';
    }
}

/**
 * Format date for input field (YYYY-MM-DD)
 * @param {string} dateString - ISO date string
 * @returns {string} Formatted date string for input field
 */
function formatDateForInput(dateString) {
    try {
        const date = new Date(dateString);
        return date.toISOString().split('T')[0];
    } catch (error) {
        console.error('Error formatting date for input:', error);
        return dateString || '';
    }
}





