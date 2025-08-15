/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./index.html", "./src/**/*.{ts,tsx,js,jsx}"],
    theme: {
        extend: {
            sans: fontFamily.sans,
        },
    },
    plugins: [require("tailwindcss-animate")],
}