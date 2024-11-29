document.addEventListener('DOMContentLoaded', async function () {
    await showUserOnNavbar()
    await fillTableOfAllUsers();
    await fillTableAboutCurrentUser();
    await addNewUserForm();
    await DeleteModalHandler();
    await EditModalHandler();
});

const ROLE_USER = {id: 2, roleName: "ROLE_USER"};
const ROLE_ADMIN = {id: 1, roleName: "ROLE_ADMIN"};

//логин пользователя в навбаре

async function showUserOnNavbar() {
    const currentUserNavbar = document.getElementById("currentUserNavbar")
    const currentUser = await dataAboutCurrentUser();
    currentUserNavbar.innerHTML =
        `<strong>${currentUser.login}</strong>
                 with roles: 
                 ${currentUser.roles.map(role => role.role.replace('ROLE_', '')).join(' ')}`;
}

async function createNewUser(user) {
    await fetch("/api/admin",
        {method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(user)})
}

async function addNewUserForm() {
    const newUserForm = document.getElementById("newUser");

    newUserForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const login = newUserForm.querySelector("#login").value.trim();
        const name = newUserForm.querySelector("#name").value.trim();
        const lastname = newUserForm.querySelector("#lastname").value.trim();
        const age = newUserForm.querySelector("#age").value.trim();
        const email = newUserForm.querySelector("#email").value.trim();
        const password = newUserForm.querySelector("#password").value.trim();

        const rolesSelected = document.getElementById("roles");

        let roles = [];
        for (let option of rolesSelected.selectedOptions) {
            if (option.value === ROLE_USER.roleName) {
                roles.push(ROLE_USER);
            } else if (option.value === ROLE_ADMIN.roleName) {
                roles.push(ROLE_ADMIN);
            }
        }

        const newUserData = {
            login: login,
            name: name,
            lastname: lastname,
            age: age,
            email: email,
            password: password,
            roles: roles
        };

        await createNewUser(newUserData);
        newUserForm.reset();

        document.querySelector('a#show-users-table').click();
        await fillTableOfAllUsers();
    });
}

async function deleteUserData(userId) {
    await fetch(`/api/admin/${userId}`, {method: 'DELETE'});
}

const modalDelete = document.getElementById("deleteModal");

async function DeleteModalHandler() {
    await fillModal(modalDelete);
}

const formDelete = document.getElementById("modalBodyDelete");
formDelete.addEventListener("submit", async function (event) {
        event.preventDefault();

        const userId = event.target.querySelector("#idDelete").value;
        await deleteUserData(userId);
        await fillTableOfAllUsers();

        const modalBootstrap = bootstrap.Modal.getInstance(modalDelete);
        modalBootstrap.hide();
    }
)

async function sendDataEditUser(user) {
    await fetch("/api/admin",
        {method: "PUT", headers: {'Content-type': 'application/json'}, body: JSON.stringify(user)})
}

const modalEdit = document.getElementById("editModal");

async function EditModalHandler() {
    await fillModal(modalEdit);
}

modalEdit.addEventListener("submit", async function (event) {
    event.preventDefault();

    const rolesSelected = document.getElementById("rolesEdit");

    let roles = [];
    for (let option of rolesSelected.selectedOptions) {
        if (option.value === ROLE_USER.roleName) {
            roles.push(ROLE_USER);
        } else if (option.value === ROLE_ADMIN.roleName) {
            roles.push(ROLE_ADMIN);
        }
    }

    let user = {
        id: document.getElementById("idEdit").value,
        login: document.getElementById("loginEdit").value,
        name: document.getElementById("nameEdit").value,
        lastname: document.getElementById("lastnameEdit").value,
        age: document.getElementById("ageEdit").value,
        email: document.getElementById("emailEdit").value,
        password: document.getElementById("passwordEdit").value,
        roles: roles
    }

    await sendDataEditUser(user);
    await fillTableOfAllUsers();

    const modalBootstrap = bootstrap.Modal.getInstance(modalEdit);
    modalBootstrap.hide();
})

async function getUserDataById(userId) {
    const response = await fetch(`/api/admin/${userId}`);
    return await response.json();
}

async function fillModal(modal) {

    modal.addEventListener("show.bs.modal", async function (event) {

        const userId = event.relatedTarget.dataset.userId;
        const user = await getUserDataById(userId);

        const modalBody = modal.querySelector(".modal-body");

        const idInput = modalBody.querySelector("input[data-user-id='id']");
        const loginInput = modalBody.querySelector("input[data-user-id='login']");
        const nameInput = modalBody.querySelector("input[data-user-id='name']");
        const lastnameInput = modalBody.querySelector("input[data-user-id='lastname']");
        const ageInput = modalBody.querySelector("input[data-user-id='age']");
        const emailInput = modalBody.querySelector("input[data-user-id='email']");
        const passwordInput = modalBody.querySelector("input[data-user-id='']");

        idInput.value = user.id;
        loginInput.value = user.login;
        nameInput.value = user.name;
        lastnameInput.value = user.lastname;
        ageInput.value = user.age;
        emailInput.value = user.email;
        passwordInput.value = user.password;


        let rolesSelect = HTMLSelectElement;

        let rolesSelectDelete = modalBody.querySelector("select[data-user-id='rolesDelete']");
        let rolesSelectEdit = modalBody.querySelector("select[data-user-id='rolesEdit']");
        let userRolesHTML = "";

        if (rolesSelectDelete !== null) {
            rolesSelect = rolesSelectDelete;
            for (let i = 0; i < user.roles.length; i++) {
                userRolesHTML +=
                    `<option value="${user.roles[i].role}">${user.roles[i].role.replace('ROLE_', '')}</option>`;
            }
        } else if (rolesSelectEdit !== null) {
            rolesSelect = rolesSelectEdit;
            userRolesHTML +=
                `<option value="ROLE_USER">USER</option>
                 <option value="ROLE_ADMIN">ADMIN</option>`
        }

        rolesSelect.innerHTML = userRolesHTML;
    })
}

async function dataAboutAllUsers() {
    const response = await fetch("/api/admin");
    return await response.json();
}

async function dataAboutCurrentUser() {
    const response = await fetch("/api/user")
    return await response.json();
}

//заполнение таблицы всех пользователей в Admin Panel
async function fillTableOfAllUsers() {
    const usersTable = document.getElementById("usersTable");
    const users = await dataAboutAllUsers();


    let usersTableHTML = "";
    for (let user of users) {
        usersTableHTML +=
            `<tr>
                <td>${user.id}</td>
                <td>${user.login}</td>
                <td>${user.name}</td>
                <td>${user.lastname}</td>
                <td>${user.age}</td>
                <td>${user.email}</td>
                <td>${user.roles.map(role => role.role.replace('ROLE_', '')).join(' ')}</td>
                <td>
                    <button class="btn btn-info btn-sm text-white"
                            data-bs-toggle="modal"
                            data-bs-target="#editModal"
                            data-user-id="${user.id}">
                        Edit</button>
                </td>
                <td>
                    <button class="btn btn-danger btn-sm btn-delete"
                            data-bs-toggle="modal"
                            data-bs-target="#deleteModal"
                            data-user-id="${user.id}">                     
                        Delete</button>
                </td>
            </tr>`;
    }
    usersTable.innerHTML = usersTableHTML;
}

//заполнение таблицы текущего пользователя
async function fillTableAboutCurrentUser() {
    const currentUserTable = document.getElementById("currentUserTable");
    const currentUser = await dataAboutCurrentUser();

    let currentUserTableHTML = "";
    currentUserTableHTML +=
        `<tr>
            <td>${currentUser.id}</td>
            <td>${currentUser.login}</td>
            <td>${currentUser.name}</td>
            <td>${currentUser.lastname}</td>
            <td>${currentUser.age}</td>
            <td>${currentUser.email}</td>
            <td>${currentUser.roles.map(role => role.role.replace('ROLE_', '')).join(' ')}</td>
        </tr>`
    currentUserTable.innerHTML = currentUserTableHTML;
}