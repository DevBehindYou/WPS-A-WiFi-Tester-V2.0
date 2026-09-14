import os
import math
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image, ImageDraw
from test_svg_arc import svg_arc_to_center

def render_full_icon(size=512, is_round=False):
    render_size = size * 2
    dpi = 100
    fig = plt.figure(figsize=(render_size / dpi, render_size / dpi), dpi=dpi)
    ax = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 120)
    ax.set_ylim(120, 0)
    ax.axis('off')
    
    bg_color = '#0E2235'
    fig.patch.set_facecolor(bg_color)
    ax.set_facecolor(bg_color)

    pts_per_unit = (render_size / 120.0) * (72.0 / dpi)
    
    if is_round:
        scale = 0.86
        pivot_x, pivot_y = 60.0, 49.2
        trans_x, trans_y = 0.0, 0.0
    else:
        scale = 1.0
        pivot_x, pivot_y = 60.0, 60.0
        trans_x, trans_y = 0.0, 0.0
        
    def transform(x, y):
        xt = pivot_x + (x - pivot_x) * scale + trans_x
        yt = pivot_y + (y - pivot_y) * scale + trans_y
        return xt, yt

    lw_main = 4.5 * pts_per_unit * scale
    lw_ring = 1.5 * pts_per_unit * scale

    # Waves & Dish
    for (x1, y1, rx, ry, phi, large, sweep, x2, y2, col) in [
        (30, 25, 42, 42, 0, 0, 1, 90, 25, '#1FB6A5'),
        (39, 35, 29, 29, 0, 0, 1, 81, 35, '#1FB6A5'),
        (51, 56, 13, 13, 0, 1, 0, 69, 56, '#F4FBF9'),
    ]:
        cx, cy, rx, ry, phi, t1, t2 = svg_arc_to_center(x1, y1, rx, ry, phi, large, sweep, x2, y2)
        t = np.linspace(t1, t2, 250)
        x = cx + rx * np.cos(t)
        y = cy + ry * np.sin(t)
        xt, yt = transform(x, y)
        ax.plot(xt, yt, color=col, linewidth=lw_main, solid_capstyle='round', solid_joinstyle='round')

    # Stem and base
    x_stem, y_stem = transform(np.array([60, 60]), np.array([56, 86]))
    ax.plot(x_stem, y_stem, color='#F4FBF9', linewidth=lw_main, solid_capstyle='round', solid_joinstyle='round')
    x_base, y_base = transform(np.array([49, 71]), np.array([86, 86]))
    ax.plot(x_base, y_base, color='#F4FBF9', linewidth=lw_main, solid_capstyle='round', solid_joinstyle='round')

    # Pulse ring
    c_x, c_y = transform(60, 45)
    ring = plt.Circle((c_x, c_y), 9 * scale, facecolor='none', edgecolor='#1FB6A5', alpha=0.25, linewidth=lw_ring)
    ax.add_patch(ring)

    # Central dot
    dot = plt.Circle((c_x, c_y), 5 * scale, facecolor='#1FB6A5', edgecolor='none')
    ax.add_patch(dot)

    temp_path = f'temp_full_{size}_{is_round}.png'
    fig.savefig(temp_path, format='png', dpi=dpi, facecolor=bg_color, edgecolor='none')
    plt.close(fig)
    
    img = Image.open(temp_path).convert("RGBA")
    
    if is_round:
        mask = Image.new("L", (render_size, render_size), 0)
        draw = ImageDraw.Draw(mask)
        draw.ellipse((0, 0, render_size, render_size), fill=255)
        res = Image.new("RGBA", (render_size, render_size), (0, 0, 0, 0))
        res.paste(img, (0, 0), mask=mask)
    else:
        corner_r = int(render_size * (24.0 / 120.0))
        mask = Image.new("L", (render_size, render_size), 0)
        draw = ImageDraw.Draw(mask)
        draw.rounded_rectangle((0, 0, render_size, render_size), radius=corner_r, fill=255)
        res = Image.new("RGBA", (render_size, render_size), (0, 0, 0, 0))
        res.paste(img, (0, 0), mask=mask)

    if os.path.exists(temp_path):
        os.remove(temp_path)
        
    final_img = res.resize((size, size), Image.Resampling.LANCZOS)
    return final_img

def render_foreground_icon(size=432):
    render_size = size * 2
    dpi = 100
    fig = plt.figure(figsize=(render_size / dpi, render_size / dpi), dpi=dpi)
    ax = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 108)
    ax.set_ylim(108, 0)
    ax.axis('off')
    
    fig.patch.set_facecolor('none')
    fig.patch.set_alpha(0.0)
    ax.set_facecolor('none')

    pts_per_unit = (render_size / 108.0) * (72.0 / dpi)
    
    scale = 0.70
    pivot_x, pivot_y = 60.0, 49.2
    trans_x, trans_y = -6.0, 4.8
    
    def transform(x, y):
        xt = pivot_x + (x - pivot_x) * scale + trans_x
        yt = pivot_y + (y - pivot_y) * scale + trans_y
        return xt, yt

    lw_main = 4.5 * scale * pts_per_unit
    lw_ring = 1.5 * scale * pts_per_unit

    # Waves & Dish
    for (x1, y1, rx, ry, phi, large, sweep, x2, y2, col) in [
        (30, 25, 42, 42, 0, 0, 1, 90, 25, '#1FB6A5'),
        (39, 35, 29, 29, 0, 0, 1, 81, 35, '#1FB6A5'),
        (51, 56, 13, 13, 0, 1, 0, 69, 56, '#F4FBF9'),
    ]:
        cx, cy, rx, ry, phi, t1, t2 = svg_arc_to_center(x1, y1, rx, ry, phi, large, sweep, x2, y2)
        t = np.linspace(t1, t2, 250)
        x = cx + rx * np.cos(t)
        y = cy + ry * np.sin(t)
        xt, yt = transform(x, y)
        ax.plot(xt, yt, color=col, linewidth=lw_main, solid_capstyle='round', solid_joinstyle='round')

    # Stem and base
    x_stem, y_stem = transform(np.array([60, 60]), np.array([56, 86]))
    ax.plot(x_stem, y_stem, color='#F4FBF9', linewidth=lw_main, solid_capstyle='round', solid_joinstyle='round')
    x_base, y_base = transform(np.array([49, 71]), np.array([86, 86]))
    ax.plot(x_base, y_base, color='#F4FBF9', linewidth=lw_main, solid_capstyle='round', solid_joinstyle='round')

    # Pulse ring
    c_x, c_y = transform(60, 45)
    ring = plt.Circle((c_x, c_y), 9 * scale, facecolor='none', edgecolor='#1FB6A5', alpha=0.25, linewidth=lw_ring)
    ax.add_patch(ring)

    # Central dot
    dot = plt.Circle((c_x, c_y), 5 * scale, facecolor='#1FB6A5', edgecolor='none')
    ax.add_patch(dot)

    temp_path = f'temp_fg_{size}.png'
    fig.savefig(temp_path, format='png', dpi=dpi, transparent=True)
    plt.close(fig)
    
    img = Image.open(temp_path).convert("RGBA")
    if os.path.exists(temp_path):
        os.remove(temp_path)
    final_img = img.resize((size, size), Image.Resampling.LANCZOS)
    return final_img

def main():
    base_res = os.path.join("app", "src", "main", "res")
    
    # 1. Root icon.png
    print("Generating root icon.png (512x512)...")
    icon_512 = render_full_icon(512, is_round=False)
    icon_512.save("icon.png", format="PNG")
    print("Saved icon.png")
    
    # 2. Mipmap densities
    densities = {
        "mipmap-mdpi": (48, 108),
        "mipmap-hdpi": (72, 162),
        "mipmap-xhdpi": (96, 216),
        "mipmap-xxhdpi": (144, 324),
        "mipmap-xxxhdpi": (192, 432),
    }
    
    for folder, (app_size, fg_size) in densities.items():
        folder_path = os.path.join(base_res, folder)
        os.makedirs(folder_path, exist_ok=True)
        
        print(f"Generating for {folder}: square={app_size}x{app_size}, round={app_size}x{app_size}, fg={fg_size}x{fg_size}")
        
        # Standard icon
        sq = render_full_icon(app_size, is_round=False)
        sq.save(os.path.join(folder_path, "ic_launcher.png"), format="PNG")
        
        # Round icon
        rd = render_full_icon(app_size, is_round=True)
        rd.save(os.path.join(folder_path, "ic_launcher_round.png"), format="PNG")
        
        # Foreground icon
        fg = render_foreground_icon(fg_size)
        fg.save(os.path.join(folder_path, "ic_launcher_foreground.png"), format="PNG")
        
    print("\nAll mipmap launcher assets successfully generated!")

if __name__ == '__main__':
    main()
