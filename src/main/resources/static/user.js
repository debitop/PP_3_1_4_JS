document.addEventListener('DOMContentLoaded', async function () {
    await showUserOnNavbar()
    await fillTableAboutUser();
});

async function dataAboutCurrentUser() {
    const response = await fetch("/api/user")
    return await response.json();
}

async function fillTableAboutUser() {
    const currentUserTable1 = document.getElementById("currentUserTable");
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
    currentUserTable1.innerHTML = currentUserTableHTML;
}

async function showUserOnNavbar() {
    const currentUserNavbar = document.getElementById("currentUserNavbar")
    const currentUser = await dataAboutCurrentUser();
    currentUserNavbar.innerHTML =
        `<strong>${currentUser.login}</strong>
                 with roles: 
                 ${currentUser.roles.map(role => role.role.replace('ROLE_', '')).join(' ')}`;
}