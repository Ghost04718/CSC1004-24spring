import sys
import tkinter
import random

class SnakeGame:
    def __init__(self, master, width=400, height=400, block_size=20):
        self.master = master
        self.width = width
        self.height = height
        self.block_size = block_size
        self.canvas = tkinter.Canvas(master, width=self.width, height=self.height, bg='black')
        self.canvas.pack()

        self.score_label = tkinter.Label(master, text="Score: 0", fg="white", bg="black")
        self.score_label.pack()
        self.new_game_button = tkinter.Button(master, text="New Game", command=self.new_game)
        self.new_game_button.pack()
        self.pause_button = tkinter.Button(master, text="Pause", command=self.pause_game)
        self.pause_button.pack()
        self.continue_button = tkinter.Button(master, text="Continue", command=self.continue_game)
        self.continue_button.pack()
        self.exit_button = tkinter.Button(master, text="Exit", command=self.exit_game)
        self.exit_button.pack()

        self.new_game()

        self.master.bind("<KeyPress>", self.change_direction)
        self.update()

    def new_game(self):
        self.canvas.delete("all")

        self.snake = [(100, 100), (80, 100), (60, 100)]
        self.direction = "Right"
        self.food = self.create_food()

        self.score = 0
        self.score_label.config(text=f"Score: {self.score}")
        self.game_over = False
        self.paused = False

        self.update()

    def exit_game(self):
        sys.exit()

    def pause_game(self):
        self.paused = True

    def continue_game(self):
        self.paused = False
        self.update()

    def create_food(self):
        while True:
            x = random.randint(0, (self.width-self.block_size) // self.block_size) * self.block_size
            y = random.randint(0, (self.height-self.block_size) // self.block_size) * self.block_size
            if (x, y) not in self.snake:
                return x, y
    
    def draw_food(self):
        self.canvas.delete("food")
        x, y = self.food
        self.canvas.create_oval(x, y, x+self.block_size, y+self.block_size, fill='red', tags="food")

    def draw_snake(self):
        self.canvas.delete("snake")
        for segment in self.snake:
            x, y = segment
            self.canvas.create_rectangle(x, y, x+self.block_size, y+self.block_size, fill='green', tags="snake")

    def move_snake(self):
        head = self.snake[0]
        x, y = head
        if self.direction == "Right":
            new_head = (x + self.block_size, y)
        elif self.direction == "Left":
            new_head = (x - self.block_size, y)
        elif self.direction == "Up":
            new_head = (x, y - self.block_size)
        elif self.direction == "Down":
            new_head = (x, y + self.block_size)

        self.snake.insert(0, new_head)
        if new_head == self.food:
            self.score += 1
            self.food = self.create_food()
        else:
            self.snake.pop()

    def change_direction(self, event):
        if event.keysym in ["Up", "Down", "Left", "Right"]:
            self.direction = event.keysym

    def update(self):
        if (self.check_collision()):
            return
        if (not self.paused) and (not self.game_over):
            self.move_snake()
            self.draw_snake()
            self.draw_food()
            self.score_label.config(text=f"Score: {self.score}")
            self.master.after(200, self.update)

    def check_collision(self):
        head = self.snake[0]
        x, y = head
        if x < 0 or x >= self.width or y < 0 or y >= self.height or head in self.snake[1:]:
            self.game_over = True
            self.canvas.create_text(self.width//2, self.height//2, text=f"Game Over! Score: {self.score}", fill="white", font=("Helvetica", 24))
        return self.game_over

def main():
    root = tkinter.Tk()
    root.title("Snake Game")
    game = SnakeGame(root)
    root.mainloop()

if __name__ == "__main__":
	main()