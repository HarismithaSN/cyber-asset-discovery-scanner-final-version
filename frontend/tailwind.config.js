/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Arial', 'Helvetica', 'sans-serif'],
      },
      spacing: {
        gutter: '8px',
      },
      minHeight: {
        'touch': '44px',
      },
      colors: {
        primary: '#1B4F8A',
      },
    },
  },
  plugins: [],
}