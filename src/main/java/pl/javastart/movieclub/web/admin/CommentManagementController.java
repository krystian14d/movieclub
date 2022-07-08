package pl.javastart.movieclub.web.admin;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.javastart.movieclub.domain.comment.Comment;
import pl.javastart.movieclub.domain.comment.CommentService;
import pl.javastart.movieclub.domain.exception.CommentNotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
@Controller
public class CommentManagementController {

    private final CommentService commentService;

    @GetMapping("/admin/edit-comment/{commentId}")
    public String editComment(@PathVariable long commentId, Model model) throws CommentNotFoundException {
        Comment comment = commentService.findCommentById(commentId);
        model.addAttribute("comment", comment);
        return "/admin/edit-comment-form";
    }

    @PostMapping("/admin/edit-comment")
    public void saveEditedComment(Comment comment, HttpServletResponse response) throws IOException {
        Comment updatedComment = commentService.updateComment(comment);
//        return "/redirect:/movie/" + updatedComment.getMovie().getId().toString();
        response.sendRedirect("/movie/ + updatedComment.getMovie().getId().toString()");
    }
}

