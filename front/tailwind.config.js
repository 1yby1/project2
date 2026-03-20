/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./public/index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#1677ff',
        'success': '#52c41a',
        'warning': '#faad14',
        'error': '#ff4d4f',
        'bg-base': '#f5f5f5',
      },
    },
  },
  plugins: [],
}
