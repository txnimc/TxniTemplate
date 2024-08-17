import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  lang: 'en-US',
  title: "TxniTemplate",
  description: "Minecraft Template Mod & Multiversion Library",
  cleanUrls: true,
  appearance: 'dark',

  head: [[
    'link',
    { rel: 'icon', sizes: '32x32', href: '/assets/blahaj-min.png' },
  ]],

  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    outline: {
      level: "deep"
    },
    logo: "/assets/blahaj-min.png",
    search: {
      provider: 'local'
    },
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Getting Started', link: '/introduction' }
    ],

    sidebar: [
      {
        text: 'Template Setup',
        items: [
          { text: 'Introduction', link: '/introduction' },
          { text: 'Getting Started', link: '/setup' },
          { text: 'IntelliJ Setup', link: '/intellij' },
          { text: 'Dependencies', link: '/dependencies' },
          { text: 'Flavors', link: '/flavors' },
        ]
      },
      {
        text: 'Library Usage',
        items: [
          { text: 'TxniLib', link: '/lib' }
        ]
      },
      {
        text: 'Other Resources',
        items: [
          { text: 'Multiversion Tips', link: '/tips' },
          { text: 'Helpful Guides', link: '/guides' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/txnimc/TxniTemplate' },
      { icon: 'discord', link: 'https://discord.gg/kS7auUeYmc'}
    ]
  }
})
